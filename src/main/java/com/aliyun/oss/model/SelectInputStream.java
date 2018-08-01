package com.aliyun.oss.model;

import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static com.aliyun.oss.event.ProgressPublisher.publishSelectProgress;

public class SelectInputStream extends FilterInputStream {
    /**
     * Format of data frame
     * |--frame type(4 bytes)--|--payload length(4 bytes)--|--header checksum(4 bytes)--|
     * |--scanned data bytes(8 bytes)--|--payload--|--payload checksum(4 bytes)--|
     */
    private static final int DATA_FRAME_MAGIC = 8388609;

    /**
     * Format of continuous frame
     * |--frame type(4 bytes)--|--payload length(4 bytes)--|--header checksum(4 bytes)--|
     * |--scanned data bytes(8 bytes)--|--payload checksum(4 bytes)--|
     */
    private static final int CONTINUOUS_FRAME_MAGIC = 8388612;

    /**
     * Format of end frame
     * |--frame type(4 bytes)--|--payload length(4 bytes)--|--header checksum(4 bytes)--|
     * |--scanned data bytes(8 bytes)--|--total scan size(8 bytes)--|
     * |--status code(4 bytes)--|--error message--|--payload checksum(4 bytes)--|
     */
    private static final int END_FRAME_MAGIC = 8388613;
    private static final int SELECT_VERSION = 1;
    private static final long DEFAULT_NOTIFICATION_THRESHOLD = 50 * 1024 * 1024;//notify every scanned 50MB

    private long currentFrameOffset;
    private long currentFramePayloadLength;
    private byte[] currentFrameTypeBytes;
    private byte[] currentFramePayloadLengthBytes;
    private byte[] currentFrameHeaderChecksumBytes;
    private byte[] scannedDataBytes;
    private byte[] currentFramePayloadChecksumBytes;
    private boolean finished;
    private ProgressListener selectProgressListener;
    private long nextNotificationScannedSize;
    /**
     * payload checksum is the last 4 bytes in one frame, we use this flag to indicate whether we
     * need read the 4 bytes before we advance to next frame.
     */
    private boolean firstReadFrame;

    public SelectInputStream(InputStream in, ProgressListener selectProgressListener) {
        super(in);
        currentFrameOffset = 0;
        currentFramePayloadLength = 0;
        currentFrameTypeBytes = new byte[4];
        currentFramePayloadLengthBytes = new byte[4];
        currentFrameHeaderChecksumBytes = new byte[4];
        scannedDataBytes = new byte[8];
        currentFramePayloadChecksumBytes = new byte[4];
        finished = false;
        firstReadFrame = true;
        this.selectProgressListener = selectProgressListener;
        this.nextNotificationScannedSize = DEFAULT_NOTIFICATION_THRESHOLD;
    }

    private void internalRead(byte[] buf, int off, int len) throws IOException {
        int bytesRead = 0;
        while (bytesRead < len) {
            int bytes = in.read(buf, off + bytesRead, len - bytesRead);
            if (bytes < 0) {
                throw new IOException("invalid input stream end found, need another " + (len - bytesRead) + "bytes");
            }
            bytesRead += bytes;
        }
    }

    private void readFrame() throws IOException {
        while (currentFrameOffset >= currentFramePayloadLength && !finished) {
            if (!firstReadFrame) {
                internalRead(currentFramePayloadChecksumBytes, 0, 4);
            }
            firstReadFrame = false;
            //advance to next frame
            internalRead(currentFrameTypeBytes, 0, 4);
            //first byte is version byte
            if (currentFrameTypeBytes[0] != SELECT_VERSION) {
                throw new IOException("invalid select version found: " + currentFrameTypeBytes[0] + ", expect: " + SELECT_VERSION);
            }
            internalRead(currentFramePayloadLengthBytes, 0, 4);
            internalRead(currentFrameHeaderChecksumBytes, 0, 4);
            internalRead(scannedDataBytes, 0, 8);

            currentFrameTypeBytes[0] = 0;
            int type = ByteBuffer.wrap(currentFrameTypeBytes).getInt();
            switch (type) {
                case DATA_FRAME_MAGIC:
                    currentFramePayloadLength = ByteBuffer.wrap(currentFramePayloadLengthBytes).getInt() - 8;
                    currentFrameOffset = 0;
                    break;
                case CONTINUOUS_FRAME_MAGIC:
                    //just break, continue
                    break;
                case END_FRAME_MAGIC:
                    currentFramePayloadLength = ByteBuffer.wrap(currentFramePayloadLengthBytes).getInt() - 8;
                    byte[] totalScannedDataSizeBytes = new byte[8];
                    internalRead(totalScannedDataSizeBytes, 0, 8);

                    byte[] statusBytes = new byte[4];
                    internalRead(statusBytes, 0, 4);

                    int status = ByteBuffer.wrap(statusBytes).getInt();
                    int errorMessageSize = (int)(currentFramePayloadLength - 12);
                    if (errorMessageSize > 0) {
                        byte[] errorMessageBytes = new byte[errorMessageSize];
                        internalRead(errorMessageBytes, 0, errorMessageSize);
                        String error = new String(errorMessageBytes);
                        if (status / 100 != 2) {
                            throw new IOException("Oss Select encounter error: code: " + status + ", message: " + error);
                        }
                    }
                    finished = true;
                    currentFramePayloadLength = currentFrameOffset;
                    internalRead(currentFramePayloadChecksumBytes, 0, 4);
                    break;
                default:
                    throw new IOException("unsupported frame type found: " + type);
            }
            //notify select progress
            ProgressEventType eventType = ProgressEventType.SELECT_SCAN_EVENT;
            if (finished) {
                eventType = ProgressEventType.SELECT_COMPLETED_EVENT;
            }
            long scannedDataSize = ByteBuffer.wrap(scannedDataBytes).getLong();
            if (scannedDataSize >= nextNotificationScannedSize || finished) {
                publishSelectProgress(selectProgressListener, eventType, scannedDataSize);
                nextNotificationScannedSize += DEFAULT_NOTIFICATION_THRESHOLD;
            }
        }
    }

    @Override
    public int read() throws IOException {
        readFrame();
        int byteRead = in.read();
        if (byteRead >= 0) {
            currentFrameOffset++;
        }
        return byteRead;
    }

    @Override
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        readFrame();
        int bytesToRead = (int)Math.min(len, currentFramePayloadLength - currentFrameOffset);
        if (bytesToRead != 0) {
            int bytes = in.read(buf, off, bytesToRead);
            if (bytes > 0) {
                currentFrameOffset += bytes;
            }
            return bytes;
        }
        return -1;
    }

    @Override
    public int available() throws IOException {
        throw new IOException("select object input stream does not support available() operation");
    }
}
