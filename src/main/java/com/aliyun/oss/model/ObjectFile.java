package com.aliyun.oss.model;

import java.util.List;

public class ObjectFile {
    private String uri;
    private String filename;
    private long size;
    private String fileModifiedTime;
    private String fileCreateTime;
    private String fileAccessTime;
    private String ossObjectType;
    private String ossStorageClass;
    private String objectACL;
    private String eTag;
    private String ossCRC64;
    private int ossTaggingCount;
    private OSSTagging ossTagging;
    private OSSUserMeta ossUserMeta;
    private String serverSideEncryption;
    private String serverSideEncryptionCustomerAlgorithm;
    private String produceTime;
    private String contentType;
    private String mediaType;
    private String latLong;
    private String title;
    private String ossExpiration;
    private String accessControlAllowOrigin;
    private String accessControlRequestMethod;
    private String serverSideDataEncryption;
    private String serverSideEncryptionKeyId;
    private String cacheControl;
    private String contentDisposition;
    private String contentEncoding;
    private String contentLanguage;
    private long imageHeight;
    private long imageWidth;
    private long videoHeight;
    private long videoWidth;
    private List<MetaQueryVideoStream> metaQueryVideoStreams;
    private List<MetaQueryAudioStream> metaQueryAudioStreams;
    private List<MetaQuerySubtitle> metaQuerySubtitles;
    private long bitrate;
    private String artist;
    private String albumArtist;
    private String composer;
    private String performer;
    private String album;
    private double duration;
    private List<MetaQueryAddress> addresses;



    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFileModifiedTime() {
        return fileModifiedTime;
    }

    public void setFileModifiedTime(String fileModifiedTime) {
        this.fileModifiedTime = fileModifiedTime;
    }

    public String getFileCreateTime() {
        return fileCreateTime;
    }

    public void setFileCreateTime(String fileCreateTime) {
        this.fileCreateTime = fileCreateTime;
    }

    public String getFileAccessTime() {
        return fileAccessTime;
    }

    public void setFileAccessTime(String fileAccessTime) {
        this.fileAccessTime = fileAccessTime;
    }

    public String getOssObjectType() {
        return ossObjectType;
    }

    public void setOssObjectType(String ossObjectType) {
        this.ossObjectType = ossObjectType;
    }

    public String getOssStorageClass() {
        return ossStorageClass;
    }

    public void setOssStorageClass(String ossStorageClass) {
        this.ossStorageClass = ossStorageClass;
    }

    public String getObjectACL() {
        return objectACL;
    }

    public void setObjectACL(String objectACL) {
        this.objectACL = objectACL;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public String getOssCRC64() {
        return ossCRC64;
    }

    public void setOssCRC64(String ossCRC64) {
        this.ossCRC64 = ossCRC64;
    }

    public int getOssTaggingCount() {
        return ossTaggingCount;
    }

    public void setOssTaggingCount(int ossTaggingCount) {
        this.ossTaggingCount = ossTaggingCount;
    }

    public OSSTagging getOssTagging() {
        return ossTagging;
    }

    public void setOssTagging(OSSTagging ossTagging) {
        this.ossTagging = ossTagging;
    }

    public OSSUserMeta getOssUserMeta() {
        return ossUserMeta;
    }

    public void setOssUserMeta(OSSUserMeta ossUserMeta) {
        this.ossUserMeta = ossUserMeta;
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getServerSideEncryption() {
        return serverSideEncryption;
    }

    public void setServerSideEncryption(String serverSideEncryption) {
        this.serverSideEncryption = serverSideEncryption;
    }

    public String getServerSideEncryptionCustomerAlgorithm() {
        return serverSideEncryptionCustomerAlgorithm;
    }

    public void setServerSideEncryptionCustomerAlgorithm(String serverSideEncryptionCustomerAlgorithm) {
        this.serverSideEncryptionCustomerAlgorithm = serverSideEncryptionCustomerAlgorithm;
    }

    public String getProduceTime() {
        return produceTime;
    }

    public void setProduceTime(String produceTime) {
        this.produceTime = produceTime;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOssExpiration() {
        return ossExpiration;
    }

    public void setOssExpiration(String ossExpiration) {
        this.ossExpiration = ossExpiration;
    }

    public String getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }

    public void setAccessControlAllowOrigin(String accessControlAllowOrigin) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
    }

    public String getAccessControlRequestMethod() {
        return accessControlRequestMethod;
    }

    public void setAccessControlRequestMethod(String accessControlRequestMethod) {
        this.accessControlRequestMethod = accessControlRequestMethod;
    }

    public String getServerSideDataEncryption() {
        return serverSideDataEncryption;
    }

    public void setServerSideDataEncryption(String serverSideDataEncryption) {
        this.serverSideDataEncryption = serverSideDataEncryption;
    }

    public String getServerSideEncryptionKeyId() {
        return serverSideEncryptionKeyId;
    }

    public void setServerSideEncryptionKeyId(String serverSideEncryptionKeyId) {
        this.serverSideEncryptionKeyId = serverSideEncryptionKeyId;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
    }

    public String getContentDisposition() {
        return contentDisposition;
    }

    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getContentLanguage() {
        return contentLanguage;
    }

    public void setContentLanguage(String contentLanguage) {
        this.contentLanguage = contentLanguage;
    }

    public long getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(long imageHeight) {
        this.imageHeight = imageHeight;
    }

    public long getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(long imageWidth) {
        this.imageWidth = imageWidth;
    }

    public long getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(long videoHeight) {
        this.videoHeight = videoHeight;
    }

    public long getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(long videoWidth) {
        this.videoWidth = videoWidth;
    }

    public List<MetaQueryVideoStream> getMetaQueryVideoStreams() {
        return metaQueryVideoStreams;
    }

    public void setMetaQueryVideoStreams(List<MetaQueryVideoStream> metaQueryVideoStreams) {
        this.metaQueryVideoStreams = metaQueryVideoStreams;
    }

    public List<MetaQueryAudioStream> getMetaQueryAudioStreams() {
        return metaQueryAudioStreams;
    }

    public void setMetaQueryAudioStreams(List<MetaQueryAudioStream> metaQueryAudioStreams) {
        this.metaQueryAudioStreams = metaQueryAudioStreams;
    }

    public List<MetaQuerySubtitle> getMetaQuerySubtitles() {
        return metaQuerySubtitles;
    }

    public void setMetaQuerySubtitles(List<MetaQuerySubtitle> metaQuerySubtitles) {
        this.metaQuerySubtitles = metaQuerySubtitles;
    }

    public long getBitrate() {
        return bitrate;
    }

    public void setBitrate(long bitrate) {
        this.bitrate = bitrate;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public List<MetaQueryAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<MetaQueryAddress> addresses) {
        this.addresses = addresses;
    }
}
