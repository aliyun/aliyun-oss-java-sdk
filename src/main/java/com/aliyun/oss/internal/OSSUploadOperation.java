/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyun.oss.internal;

import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.common.utils.LogUtils.logException;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;
import static com.aliyun.oss.internal.OSSUtils.ensureObjectKeyValid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.UploadFileRequest;
import com.aliyun.oss.model.UploadFileResult;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;

/**
 * OSSUploadOperation
 *
 */
public class OSSUploadOperation {
    
    static class UploadCheckPoint implements Serializable {

        private static final long serialVersionUID = 5424904565837227164L;
        
        private static final String UPLOAD_MAGIC = "FE8BB4EA-B593-4FAC-AD7A-2459A36E2E62";
        
        /**
         * 从checkpoint文件中加载checkpoint数据
         */
        public synchronized void load(String cpFile) throws IOException, ClassNotFoundException {
            FileInputStream fileIn =new FileInputStream(cpFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            UploadCheckPoint ucp = (UploadCheckPoint) in.readObject();
            assign(ucp);
            in.close();
            fileIn.close();
        }
        
        /**
         * 把checkpoint数据写到checkpoint文件
         */
        public synchronized void dump(String cpFile) throws IOException {
            this.md5 = hashCode();
            FileOutputStream fileOut = new FileOutputStream(cpFile);
            ObjectOutputStream outStream = new ObjectOutputStream(fileOut);
            outStream.writeObject(this);
            outStream.close();
            fileOut.close();
        }
        
        /**
         * 分片上传完成，更新状态
         * @throws IOException 
         */
        public synchronized void update(int partIndex, PartETag partETag, boolean completed) throws IOException {
            partETags.add(partETag);
            uploadParts.get(partIndex).isCompleted = completed;
        }
        
        /**
         * 判读本地文件与checkpoint中记录的信息是否相符，即待上传文件是否修改过
         */
        public synchronized boolean isValid(String uploadFile) {
            // 比较checkpoint的magic和md5
            if (this.magic == null || 
                    !this.magic.equals(UPLOAD_MAGIC) || 
                    this.md5 != hashCode()) {
                return false;
            }
            
            // 确认上传文件存在
            File upload = new File(uploadFile);
            if (!upload.exists()) {
                return false;
            }
            
            // 上传文件的名称、大小、最后修改时间相同
            if (!this.uploadFile.equals(uploadFile) || 
                    this.uploadFileStat.size != upload.length() ||
                    this.uploadFileStat.lastModified != upload.lastModified()) {
                return false;
            }
            
            return true;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + ((magic == null) ? 0 : magic.hashCode());
            result = prime * result + ((partETags == null) ? 0 : partETags.hashCode());
            result = prime * result + ((uploadFile == null) ? 0 : uploadFile.hashCode());
            result = prime * result + ((uploadFileStat == null) ? 0 : uploadFileStat.hashCode());
            result = prime * result + ((uploadID == null) ? 0 : uploadID.hashCode());
            result = prime * result + ((uploadParts == null) ? 0 : uploadParts.hashCode());
            return result;
        }
        
        private void assign(UploadCheckPoint ucp) {
            this.magic = ucp.magic;
            this.md5 = ucp.md5;
            this.uploadFile = ucp.uploadFile;
            this.uploadFileStat = ucp.uploadFileStat;
            this.key = ucp.key;
            this.uploadID = ucp.uploadID;
            this.uploadParts = ucp.uploadParts;
            this.partETags = ucp.partETags;
        }
        
        public String magic;
        public int md5;
        public String uploadFile;
        public FileStat uploadFileStat;
        public String key;
        public String uploadID;
        public ArrayList<UploadPart> uploadParts;
        public ArrayList<PartETag> partETags;

    }
    
    static class FileStat implements Serializable {
        private static final long serialVersionUID = -1223810339796425415L;
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((digest == null) ? 0 : digest.hashCode());
            result = prime * result + (int) (lastModified ^ (lastModified >>> 32));
            result = prime * result + (int) (size ^ (size >>> 32));
            return result;
        }
        
        public static FileStat getFileStat(String uploadFile) {
            FileStat fileStat = new FileStat();
            File file = new File(uploadFile);
            fileStat.size = file.length();
            fileStat.lastModified = file.lastModified();
            return fileStat;
        }
        
        public long size; // 文件大小
        public long lastModified; // 文件最后修改时间
        public String digest; // 文件内容摘要
    }
    
    static class UploadPart implements Serializable {
        private static final long serialVersionUID = 6692863980224332199L;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (isCompleted ? 1231 : 1237);
            result = prime * result + number;
            result = prime * result + (int) (offset ^ (offset >>> 32));
            result = prime * result + (int) (size ^ (size >>> 32));
            return result;
        }

        public int number; // 分片序号
        public long offset; // 分片在文件中的偏移量
        public long size; // 分片大小
        public boolean isCompleted; // 该分片上传是否完成
    }
    
    static class PartResult {
        
        public PartResult (int number, long offset, long length) {
            this.number = number;
            this.offset = offset;
            this.length = length;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public long getOffset() {
            return offset;
        }

        public void setOffset(long offset) {
            this.offset = offset;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        public boolean isFailed() {
            return failed;
        }

        public void setFailed(boolean failed) {
            this.failed = failed;
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }

        private int number; // 分片序号
        private long offset; // 分片在文件中的偏移
        private long length; // 分片长度
        private boolean failed; // 分片上传是否失败
        private Exception exception; // 分片上传异常
    }
    
    public OSSUploadOperation(OSSMultipartOperation multipartOperation) {
        this.multipartOperation = multipartOperation;
    }
    
    public UploadFileResult uploadFile(UploadFileRequest uploadFileRequest) throws Throwable {
        assertParameterNotNull(uploadFileRequest, "uploadFileRequest");
        
        String bucketName = uploadFileRequest.getBucketName();
        String key = uploadFileRequest.getKey();
        
        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);
        
        assertParameterNotNull(uploadFileRequest.getUploadFile(), "uploadFile");
        
        // 开启断点续传，没有指定checkpoint文件，使用默认值
        if (uploadFileRequest.isEnableCheckpoint()) {
            if (uploadFileRequest.getCheckpointFile() == null || uploadFileRequest.getCheckpointFile().isEmpty()) {
                uploadFileRequest.setCheckpointFile(uploadFileRequest.getUploadFile() + ".ucp");
            }
        }
        
        return uploadFileWithCheckpoint(uploadFileRequest);
    }
    
    private UploadFileResult uploadFileWithCheckpoint(UploadFileRequest uploadFileRequest) throws Throwable {
        UploadFileResult uploadFileResult = new UploadFileResult();
        UploadCheckPoint uploadCheckPoint = new UploadCheckPoint();
        
        // 开启断点续传，从checkpoint文件读取上次分片上传结果
        if (uploadFileRequest.isEnableCheckpoint()) {
            // 从checkpoint文件读取上次上传结果，checkpoint文件不存在/文件被篡改/被破坏时，从新上传
            try {
                uploadCheckPoint.load(uploadFileRequest.getCheckpointFile());
            } catch (Exception e) {
                remove(uploadFileRequest.getCheckpointFile());
            }
            
            // 上传的文件修改了，从新上传
            if (!uploadCheckPoint.isValid(uploadFileRequest.getUploadFile())) {
                prepare(uploadCheckPoint, uploadFileRequest);
                remove(uploadFileRequest.getCheckpointFile());
            }
        } else {
            // 没有开启断点上传功能，从新上传
            prepare(uploadCheckPoint, uploadFileRequest);
        }
        
        // 并发上传分片
        List<PartResult> partResults = upload(uploadCheckPoint, uploadFileRequest);
        for (PartResult partResult : partResults) {
            if (partResult.isFailed()) {
                throw partResult.getException();
            }
        }
        
        // 提交上传任务
        CompleteMultipartUploadResult multipartUploadResult = complete(uploadCheckPoint, uploadFileRequest);
        uploadFileResult.setMultipartUploadResult(multipartUploadResult);
        
        // 开启了断点上传，成功上传后删除checkpoint文件
        if (uploadFileRequest.isEnableCheckpoint()) {
            remove(uploadFileRequest.getCheckpointFile());
        }
        
        return uploadFileResult;
    }
    
    private void prepare(UploadCheckPoint uploadCheckPoint, UploadFileRequest uploadFileRequest) {
        uploadCheckPoint.magic = UploadCheckPoint.UPLOAD_MAGIC;
        uploadCheckPoint.uploadFile = uploadFileRequest.getUploadFile();
        uploadCheckPoint.key = uploadFileRequest.getKey();
        uploadCheckPoint.uploadFileStat = FileStat.getFileStat(uploadCheckPoint.uploadFile);
        uploadCheckPoint.uploadParts = splitFile(uploadCheckPoint.uploadFileStat.size, 
                uploadFileRequest.getPartSize());
        uploadCheckPoint.partETags = new ArrayList<PartETag>();

        InitiateMultipartUploadRequest initiateUploadRequest = new InitiateMultipartUploadRequest(
                uploadFileRequest.getBucketName(), uploadFileRequest.getKey(), 
                uploadFileRequest.getObjectMetadata());
        InitiateMultipartUploadResult initiateUploadResult = 
                multipartOperation.initiateMultipartUpload(initiateUploadRequest);
        uploadCheckPoint.uploadID = initiateUploadResult.getUploadId();
    }
    
    private ArrayList<PartResult> upload(UploadCheckPoint uploadCheckPoint, UploadFileRequest uploadFileRequest) 
            throws Throwable {
        ArrayList<PartResult> taskResults = new ArrayList<PartResult>();
        ExecutorService service = Executors.newFixedThreadPool(uploadFileRequest.getTaskNum());
        ArrayList<Future<PartResult>> futures = new ArrayList<Future<PartResult>>();
                
        for (int i = 0; i < uploadCheckPoint.uploadParts.size(); i++) {
            if (!uploadCheckPoint.uploadParts.get(i).isCompleted) {
                futures.add(service.submit(new Task(i, "upload-" + i, uploadCheckPoint, i, 
                        uploadFileRequest, multipartOperation)));
            } else {
                taskResults.add(new PartResult(i + 1, uploadCheckPoint.uploadParts.get(i).offset,
                        uploadCheckPoint.uploadParts.get(i).size));
            }
        }
        service.shutdown();
        
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        
        for (Future<PartResult> future : futures) {
            try {
                PartResult tr = future.get();
                taskResults.add(tr);
            } catch (ExecutionException e) {
                throw e.getCause();
            }
        }
        
        Collections.sort(taskResults, new Comparator<PartResult>() {
            @Override
            public int compare(PartResult p1, PartResult p2) {
                return p1.getNumber() - p2.getNumber();
            }
        });
        
        return taskResults;
    }
    
    static class Task implements Callable<PartResult> {
        
        public Task(int id, String name, UploadCheckPoint uploadCheckPoint, int partIndex,
                UploadFileRequest uploadFileRequest, OSSMultipartOperation multipartOperation) {
            this.id = id;
            this.name = name;
            this.uploadCheckPoint = uploadCheckPoint;
            this.partIndex = partIndex;
            this.uploadFileRequest = uploadFileRequest;
            this.multipartOperation = multipartOperation;
        }
        
        @Override
        public PartResult call() throws Exception {
            PartResult tr = null;
            InputStream instream = null;

            try {
                UploadPart uploadPart = uploadCheckPoint.uploadParts.get(partIndex);
                tr = new PartResult(partIndex + 1, uploadPart.offset, uploadPart.size);
                
                instream = new FileInputStream(uploadCheckPoint.uploadFile);
                instream.skip(uploadPart.offset);

                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(uploadFileRequest.getBucketName());
                uploadPartRequest.setKey(uploadFileRequest.getKey());
                uploadPartRequest.setUploadId(uploadCheckPoint.uploadID);
                uploadPartRequest.setPartNumber(uploadPart.number);
                uploadPartRequest.setInputStream(instream);
                uploadPartRequest.setPartSize(uploadPart.size);
                
                UploadPartResult uploadPartResult = multipartOperation.uploadPart(uploadPartRequest);
                
                PartETag partETag = new PartETag(uploadPartResult.getPartNumber(), uploadPartResult.getETag());
                uploadCheckPoint.update(partIndex, partETag, true);
                if (uploadFileRequest.isEnableCheckpoint()) {
                   uploadCheckPoint.dump(uploadFileRequest.getCheckpointFile()); 
                }
            } catch (Exception e) {
                tr.setFailed(true);
                tr.setException(e);
                logException(String.format("Task %d:%s upload part %d failed: ", id, name, partIndex + 1), e);
            } finally {
                if (instream != null) {
                    instream.close();
                }
            }
                                    
            return tr;
        }

        private int id;
        private String name;
        private UploadCheckPoint uploadCheckPoint;
        private int partIndex;
        private UploadFileRequest uploadFileRequest;
        private OSSMultipartOperation multipartOperation;
    }
    
    private CompleteMultipartUploadResult complete(UploadCheckPoint uploadCheckPoint, UploadFileRequest uploadFileRequest) {
        Collections.sort(uploadCheckPoint.partETags, new Comparator<PartETag>() {
            @Override
            public int compare(PartETag p1, PartETag p2) {
                return p1.getPartNumber() - p2.getPartNumber();
            }
        });
        CompleteMultipartUploadRequest completeUploadRequest = new CompleteMultipartUploadRequest(
                uploadFileRequest.getBucketName(), uploadFileRequest.getKey(), 
                uploadCheckPoint.uploadID, uploadCheckPoint.partETags);
        completeUploadRequest.setCallback(uploadFileRequest.getCallback());
        return multipartOperation.completeMultipartUpload(completeUploadRequest);
    }
    
    private ArrayList<UploadPart> splitFile(long fileSize, long partSize) {
        ArrayList<UploadPart> parts = new ArrayList<UploadPart>();
        
        long partNum = fileSize / partSize;
        if (partNum >= 10000) {
            partSize = fileSize / (10000 - 1);
            partNum = fileSize / partSize;
        }

        for (long i = 0; i < partNum; i++) {
            UploadPart part = new UploadPart();
            part.number = (int) (i + 1);
            part.offset = i * partSize;
            part.size = partSize;
            part.isCompleted = false;
            parts.add(part);
        }

        if (fileSize % partSize > 0) {
            UploadPart part = new UploadPart();
            part.number = parts.size() + 1;
            part.offset = parts.size() * partSize;
            part.size = fileSize % partSize;
            part.isCompleted = false;
            parts.add(part);
        }

        return parts;
    }
    
    private boolean remove(String filePath) {
        boolean flag = false;  
        File file = new File(filePath);  

        if (file.isFile() && file.exists()) {  
            flag = file.delete();  
        }  
        
        return flag;  
    }
    
    private OSSMultipartOperation multipartOperation;
}
