package cz.dp.share.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Ws02EmailDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String to;

    private String from;

    private String sender;

    private String subject;

    private String content;

    private List<Ws02EmailAttachmentDto> attachments;

    public Ws02EmailDto(String to, String from, String subject, String content) {
        setTo(to);
        setFrom(from);
        setSender(from);
        setSubject(subject);
        setContent(content);

        attachments = new ArrayList<Ws02EmailAttachmentDto>();
        // if(priloha!=null){
        // attachments.add(new Ws02EmailAttachmentDto(priloha.getFileName(),
        // priloha.getStream().toByteArray()));
        // }
    }
}
