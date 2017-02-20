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
import static com.aliyun.oss.internal.OSSConstants.DEFAULT_BUFFER_SIZE;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;
import static com.aliyun.oss.internal.OSSUtils.ensureObjectKeyValid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.aliyun.oss.model.DownloadFileRequest;
import com.aliyun.oss.model.DownloadFileResult;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.SimplifiedObjectMeta;

/**
 * OSSDownloadOperation
 *
 */
public class OSSDownloadOperation {
    
    static class DownloadCheckPoint implements Serializable {

        private static final long serialVersionUID = 4682293344365787077L;
        private static final String DOWNLOAD_MAGIC = "92611BED-89E2-46B6-89E5-72F273D4B0A3";
        
        /**
         * 从checkpoint文件中加载checkpoint数据
         */
        public synchronized void load(String cpFile) throws IOException, ClassNotFoundException {
            FileInputStream fileIn =new FileInputStream(cpFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            DownloadCheckPoint dcp = (DownloadCheckPoint) in.readObject();
            assign(dcp);
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
         * 分片下载完成，更新分片状态
         * @throws IOException 
         */
        public synchronized void update(int index, boolean completed) throws IOException {
            downloadParts.get(index).isCompleted = completed;
        }
        
        /**
         * 判读Object与checkpoint中记录的信息是否相符，即Object是否修改过
         */
        public synchronized boolean isValid(OSSObjectOperation objectOperation) {
            // 比较checkpoint的magic和md5
            if (this.magic == null || 
                    !this.magic.equals(DOWNLOAD_MAGIC) || 
                    this.md5 != hashCode()) {
                return false;
            }
            
            GenericRequest genericRequest = new GenericRequest(bucketName, objectKey);
            SimplifiedObjectMeta meta = objectOperation.getSimplifiedObjectMeta(genericRequest);
            
            // Object的大小、最后修改时间、ETAG相同
            if (this.objectStat.size != meta.getSize() || 
                    !this.objectStat.lastModified.equals(meta.getLastModified()) ||
                    !this.objectStat.digest.equals(meta.getETag())) {
                return false;
            }
            
            return true;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((bucketName == null) ? 0 : bucketName.hashCode());
            result = prime * result + ((downloadFile == null) ? 0 : downloadFile.hashCode());
            result = prime * result + ((magic == null) ? 0 : magic.hashCode());
            result = prime * result + ((objectKey == null) ? 0 : objectKey.hashCode());
            result = prime * result + ((objectStat == null) ? 0 : objectStat.hashCode());
            result = prime * result + ((downloadParts == null) ? 0 : downloadParts.hashCode());
            return result;
        }
        
        private void assign(DownloadCheckPoint dcp) {
            this.magic = dcp.magic;
            this.md5 = dcp.md5;
            this.downloadFile = dcp.downloadFile;
            this.bucketName = dcp.bucketName;
            this.objectKey = dcp.objectKey;
            this.objectStat = dcp.objectStat;
            this.downloadParts = dcp.downloadParts;
        }
        
        public String magic;  // magic
        public int md5;  // checkpoint内容的md5
        public String downloadFile;  // 本地文件
        public String bucketName; // bucket name
        public String objectKey;  // object key
        public ObjectStat objectStat;  // object state
        public ArrayList<DownloadPart> downloadParts;  // 分片

    }
    
    static class ObjectStat implements Serializable {

        private static final long serialVersionUID = -2883494783412999919L;
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((digest == null) ? 0 : digest.hashCode());
            result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
            result = prime * result + (int) (size ^ (size >>> 32));
            return result;
        }

        public static ObjectStat getFileStat(OSSObjectOperation objectOperation, 
                String bucketName, String key) {
            GenericRequest genericRequest = new GenericRequest(bucketName, key);
            SimplifiedObjectMeta meta = objectOperation.getSimplifiedObjectMeta(genericRequest);
            
            ObjectStat objStat = new ObjectStat();
            objStat.size = meta.getSize();
            objStat.lastModified = meta.getLastModified();
            objStat.digest = meta.getETag();
            
            return objStat;
        }
        
        public long size; // 文件大小
        public Date lastModified; // 文件最后修改时间
        public String digest; // 文件内容摘要，值为ETAG
    }
    
    static class DownloadPart implements Serializable {

        private static final long serialVersionUID = -3655925846487976207L;
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + index;
            result = prime * result + (isCompleted ? 1231 : 1237);
            result = prime * result + (int) (end ^ (end >>> 32));
            result = prime * result + (int) (start ^ (start >>> 32));
            return result;
        }

        public int index; // 分片序号，从0开始编号
        public long start; // 分片起始位置
        public long end; // 分片片结束位置
        public boolean isCompleted; // 该分片下载是否完成
    }
    
    static class PartResult {

        public PartResult(int number, long start, long end) {
            this.number = number;
            this.start = start;
            this.end = end;
        }
        
        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }

        public int getNumber() {
            return number;
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

        private int number; // 分片序号，从1开始编号
        private long start; // 分片开始位置
        private long end;  // 分片结束位置
        private boolean failed; // 分片上传是否失败
        private Exception exception; // 分片上传异常
    }
    
    static class DownloadResult {

        public List<PartResult> getPartResults() {
            return partResults;
        }

        public void setPartResults(List<PartResult> partResults) {
            this.partResults = partResults;
        }

        public ObjectMetadata getObjectMetadata() {
            return objectMetadata;
        }

        public void setObjectMetadata(ObjectMetadata objectMetadata) {
            this.objectMetadata = objectMetadata;
        }

        private List<PartResult> partResults;
        private ObjectMetadata objectMetadata;
    }
    
    public OSSDownloadOperation(OSSObjectOperation objectOperation) {
        this.objectOperation = objectOperation;
    }
    
    public DownloadFileResult downloadFile(DownloadFileRequest downloadFileRequest) throws Throwable {
        assertParameterNotNull(downloadFileRequest, "downloadFileRequest");
        
        String bucketName = downloadFileRequest.getBucketName();
        String key = downloadFileRequest.getKey();
        
        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);
        
        // 没有指定本地文件，使用key作为本地文件名称
        if (downloadFileRequest.getDownloadFile() == null) {
            downloadFileRequest.setDownloadFile(downloadFileRequest.getKey());
        }
        
        // 开启断点续传，没有指定checkpoint文件，使用默认值
        if (downloadFileRequest.isEnableCheckpoint()) {
            if (downloadFileRequest.getCheckpointFile() == null || downloadFileRequest.getCheckpointFile().isEmpty()) {
                downloadFileRequest.setCheckpointFile(downloadFileRequest.getDownloadFile() + ".dcp");
            }
        }
        
        return downloadFileWithCheckpoint(downloadFileRequest);
    }
    
    private DownloadFileResult downloadFileWithCheckpoint(DownloadFileRequest downloadFileRequest) throws Throwable {
        DownloadFileResult downloadFileResult = new DownloadFileResult();
        DownloadCheckPoint downloadCheckPoint = new DownloadCheckPoint();
        
        // 开启断点续传，从checkpoint文件读取上次分片下载的结果
        if (downloadFileRequest.isEnableCheckpoint()) {
            // 从checkpoint文件读取上次下载结果，checkpoint文件不存在/文件被篡改/被破坏时，从新下载
            try {
                downloadCheckPoint.load(downloadFileRequest.getCheckpointFile());
            } catch (Exception e) {
                remove(downloadFileRequest.getCheckpointFile());
            }
            
            // 上传的文件修改了，从新下载
            if (!downloadCheckPoint.isValid(objectOperation)) {
                prepare(downloadCheckPoint, downloadFileRequest);
                remove(downloadFileRequest.getCheckpointFile());
            }
        } else {
            // 没有开启断点下载功能，从新下载
            prepare(downloadCheckPoint, downloadFileRequest);
        }
        
        // 并发下载分片
        DownloadResult downloadResult = download(downloadCheckPoint, downloadFileRequest);
        for (PartResult partResult : downloadResult.getPartResults()) {
            if (partResult.isFailed()) {
                throw partResult.getException();
            }
        }
        
        // 重命名临时文件
        renameTo(downloadFileRequest.getTempDownloadFile(), downloadFileRequest.getDownloadFile());
        
        // 开启了断点下载，成功上传后删除checkpoint文件
        if (downloadFileRequest.isEnableCheckpoint()) {
            remove(downloadFileRequest.getCheckpointFile());
        }
        
        downloadFileResult.setObjectMetadata(downloadResult.getObjectMetadata());
        return downloadFileResult;
    }
    
    private void prepare(DownloadCheckPoint downloadCheckPoint, DownloadFileRequest downloadFileRequest) throws IOException {
        downloadCheckPoint.magic = DownloadCheckPoint.DOWNLOAD_MAGIC;
        downloadCheckPoint.downloadFile = downloadFileRequest.getDownloadFile();
        downloadCheckPoint.bucketName = downloadFileRequest.getBucketName();
        downloadCheckPoint.objectKey = downloadFileRequest.getKey();
        downloadCheckPoint.objectStat = ObjectStat.getFileStat(objectOperation, 
                downloadCheckPoint.bucketName, downloadCheckPoint.objectKey);
        downloadCheckPoint.downloadParts = splitFile(downloadCheckPoint.objectStat.size, 
                downloadFileRequest.getPartSize());
        
        createFixedFile(downloadFileRequest.getTempDownloadFile(), downloadCheckPoint.objectStat.size);
    }
    
    public static void createFixedFile(String filePath, long length) throws IOException {
        File file = new File(filePath);
        RandomAccessFile rf = null;
        
        try {
            rf = new RandomAccessFile(file, "rw");
            rf.setLength(length);
        } finally {
            if (rf != null) {
                rf.close();
            }
        }
    } 
    
    private DownloadResult download(DownloadCheckPoint downloadCheckPoint, DownloadFileRequest downloadFileRequest) 
            throws Throwable {
        DownloadResult downloadResult = new DownloadResult();
        ArrayList<PartResult> taskResults = new ArrayList<PartResult>();
        ExecutorService service = Executors.newFixedThreadPool(downloadFileRequest.getTaskNum());
        ArrayList<Future<PartResult>> futures = new ArrayList<Future<PartResult>>();
        List<Task> tasks = new ArrayList<Task>();
                
        for (int i = 0; i < downloadCheckPoint.downloadParts.size(); i++) {
            if (!downloadCheckPoint.downloadParts.get(i).isCompleted) {
                Task task = new Task(i, "download-" + i, downloadCheckPoint, i, downloadFileRequest, objectOperation);
                futures.add(service.submit(task));
                tasks.add(task);
            } else {
                taskResults.add(new PartResult(i + 1, downloadCheckPoint.downloadParts.get(i).start,
                        downloadCheckPoint.downloadParts.get(i).end));
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
        
        downloadResult.setPartResults(taskResults);
        if (tasks.size() > 0) {
            downloadResult.setObjectMetadata(tasks.get(0).GetobjectMetadata());
        }

        return downloadResult;
    }
    
    static class Task implements Callable<PartResult> {
        
        public Task(int id, String name, DownloadCheckPoint downloadCheckPoint, int partIndex,
                DownloadFileRequest downloadFileRequest, OSSObjectOperation objectOperation) {
            this.id = id;
            this.name = name;
            this.downloadCheckPoint = downloadCheckPoint;
            this.partIndex = partIndex;
            this.downloadFileRequest = downloadFileRequest;
            this.objectOperation = objectOperation;
        }
        
        @Override
        public PartResult call() throws Exception {
            PartResult tr = null;
            RandomAccessFile output = null;
            InputStream content = null;
            
            try {
                DownloadPart downloadPart = downloadCheckPoint.downloadParts.get(partIndex);
                tr = new PartResult(partIndex + 1, downloadPart.start, downloadPart.end);
                
                output = new RandomAccessFile(downloadFileRequest.getTempDownloadFile(), "rw");  
                output.seek(downloadPart.start);

                GetObjectRequest getObjectRequest = new GetObjectRequest(downloadFileRequest.getBucketName(),
                        downloadFileRequest.getKey());
                getObjectRequest.setMatchingETagConstraints(downloadFileRequest.getMatchingETagConstraints());
                getObjectRequest.setNonmatchingETagConstraints(downloadFileRequest.getNonmatchingETagConstraints());
                getObjectRequest.setModifiedSinceConstraint(downloadFileRequest.getModifiedSinceConstraint());
                getObjectRequest.setUnmodifiedSinceConstraint(downloadFileRequest.getUnmodifiedSinceConstraint());
                getObjectRequest.setResponseHeaders(downloadFileRequest.getResponseHeaders());
                getObjectRequest.setRange(downloadPart.start, downloadPart.end);
                
                OSSObject ossObj = objectOperation.getObject(getObjectRequest);
                objectMetadata = ossObj.getObjectMetadata();
                content = ossObj.getObjectContent();
                
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int bytesRead = 0;
                while ((bytesRead = content.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                
                downloadCheckPoint.update(partIndex, true);
                if (downloadFileRequest.isEnableCheckpoint()) {
                   downloadCheckPoint.dump(downloadFileRequest.getCheckpointFile()); 
                }
            } catch (Exception e) {
                tr.setFailed(true);
                tr.setException(e);
                logException(String.format("Task %d:%s upload part %d failed: ", id, name, partIndex), e);
            } finally {
                if (output != null) {
                    output.close();
                }
                
                if (content != null) {
                    content.close();
                }
            }
                                    
            return tr;
        }
        
        public ObjectMetadata GetobjectMetadata () {
            return objectMetadata;
        }

        private int id;
        private String name;
        private DownloadCheckPoint downloadCheckPoint;
        private int partIndex;
        private DownloadFileRequest downloadFileRequest;
        private OSSObjectOperation objectOperation;
        private ObjectMetadata objectMetadata;
    }
    
    private ArrayList<DownloadPart> splitFile(long objectSize, long partSize) {
        ArrayList<DownloadPart> parts = new ArrayList<DownloadPart>();
        
        long partNum = objectSize / partSize;
        if (partNum >= 10000) {
            partSize = objectSize / (10000 - 1);
        }

        long offset = 0L;
        for (int i = 0; offset < objectSize; offset += partSize, i++) {
            DownloadPart part = new DownloadPart();
            part.index = i;
            part.start = offset;
            part.end = getPartEnd(offset, objectSize, partSize);
            parts.add(part);
        }

        return parts;
    }
    
    private long getPartEnd(long begin, long total, long per) {
        if (begin + per > total) {
            return total - 1;
        }
        return begin + per - 1;
    }
    
    private boolean remove(String filePath) {
        boolean flag = false;  
        File file = new File(filePath);  

        if (file.isFile() && file.exists()) {  
            flag = file.delete();  
        }  
        
        return flag;  
    }
    
    private static void renameTo(String srcFilePath, String destFilePath) throws IOException {
        File srcfile =new File(srcFilePath);
        File destfile =new File(destFilePath);
        moveFile(srcfile, destfile);
    }
    
	private static void moveFile(final File srcFile, final File destFile) throws IOException {
		if (srcFile == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (destFile == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (!srcFile.exists()) {
			throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
		}
		if (srcFile.isDirectory()) {
			throw new IOException("Source '" + srcFile + "' is a directory");
		}
		if (destFile.isDirectory()) {
			throw new IOException("Destination '" + destFile + "' is a directory");
		}
		if (destFile.exists()) {
			if (!destFile.delete()) {
				throw new IOException("Failed to delete original file '" + srcFile + "'");
			}
		}

		final boolean rename = srcFile.renameTo(destFile);
		if (!rename) {
			copyFile(srcFile, destFile);
			if (!srcFile.delete()) {
				throw new IOException("Failed to delete original file '" + srcFile + "' after copy to '" + destFile + "'");
			}
		}
	}
        
	private static void copyFile(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[4096];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}
        
    private OSSObjectOperation objectOperation;
}
