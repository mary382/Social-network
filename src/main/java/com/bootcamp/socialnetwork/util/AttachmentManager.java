package com.bootcamp.socialnetwork.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AttachmentManager {

    private static final Set<String> IMAGE_EXTENSION =
            Stream.of("jpeg", "jpg", "png", "bmp", "svg", "gif").collect(Collectors.toSet());
    private static final Set<String> AUDIO_EXTENSION =
            Stream.of("mp3").collect(Collectors.toSet());
    private static final Set<String> DOCUMENT_EXTENSION =
            Stream.of("doc", "docx", "djvu", "pdf", "pptx", "rar", "csv", "xml", "json", "xls").collect(Collectors.toSet());
    private static final Set<String> VIDEO_EXTENSION =
            Stream.of("mp4").collect(Collectors.toSet());

    public static AttachmentType getAttachmentType(String attachmentExtension){

        attachmentExtension = attachmentExtension.toLowerCase();
        if (IMAGE_EXTENSION.contains(attachmentExtension))
            return AttachmentType.IMAGE;

        if (AUDIO_EXTENSION.contains(attachmentExtension))
            return AttachmentType.AUDIO;

        if (DOCUMENT_EXTENSION.contains(attachmentExtension))
            return AttachmentType.DOCUMENT;

        if (VIDEO_EXTENSION.contains(attachmentExtension))
            return AttachmentType.VIDEO;

        return null;
    }

    public static String getFileExtension(String filename){
        return FilenameUtils.getExtension(filename);
    }
}
