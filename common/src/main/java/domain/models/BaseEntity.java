package domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import util.EntityHelper;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static util.EntityHelper.epochToDateString;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {
    @Id
    @GeneratedValue Long id;

    @CreatedDate
    private Long createDate;

    @LastModifiedDate
    private Long lastModifiedDate;

    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

//    public String getLastModifiedDateString(){
//        return epochToDateString(getLastModifiedDate());
//    }
//
//    public String getCreateDateString(){
//        return epochToDateString(getCreateDate());
//    }

    @Override
    public String toString(){
        try {
            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);
            String val = om.writeValueAsString(this);
//            val+=String.format(" createDate: %s , lastModifiedDate: %s\n",
//                   epochToDateString(getCreateDate()), epochToDateString(getLastModifiedDate()));
            return val;
        } catch(JsonProcessingException e){
            e.printStackTrace();
        }
        return null;
    }

}
