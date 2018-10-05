package info.datamuse.onesky.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MultipartBodyEncoder {

    private static final String LINE_FEED = "\r\n";

    private final String boundary;
    private final List<InputStream> inputStreams = new ArrayList<>();

    public MultipartBodyEncoder() {
        this.boundary = "===" + System.nanoTime() + "===";
    }

    public String getBoundary() {
        return boundary;
    }

    public MultipartBodyEncoder addFormField(final String name, final String value) {
        final StringBuilder sb = new StringBuilder();

        sb.append("--" + boundary).append(LINE_FEED);
        sb.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        sb.append("Content-Type: text/plain; charset=UTF-8")
                .append(LINE_FEED);
        sb.append(LINE_FEED);
        sb.append(value).append(LINE_FEED);

        addPart(sb.toString());

        return this;
    }

    public MultipartBodyEncoder addFilePart(final String fieldName,
                                            final String fileName,
                                            final String contentType,
                                            final InputStream fileInputStream) {
        final StringBuilder sb = new StringBuilder();

        sb.append("--" + boundary).append(LINE_FEED);
        sb.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        sb.append(
                "Content-Type: " + contentType)
                .append(LINE_FEED);
        sb.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        sb.append(LINE_FEED);

        addPart(sb.toString());

        inputStreams.add(fileInputStream);

        addPart(LINE_FEED);

        return this;
    }

    public InputStream finish() {
        final StringBuilder sb = new StringBuilder();
        sb.append(LINE_FEED);
        sb.append("--" + boundary + "--").append(LINE_FEED);

        addPart(sb.toString());

        return new SequenceInputStream(Collections.enumeration(inputStreams));
    }

    private void addPart(final String partAsText) {
        inputStreams.add(new ByteArrayInputStream(partAsText.getBytes(StandardCharsets.UTF_8)));
    }
}
