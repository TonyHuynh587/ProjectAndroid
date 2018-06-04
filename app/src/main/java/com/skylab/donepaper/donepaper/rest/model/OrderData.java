package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OrderData implements Serializable {
    /**
     * id : 1539
     * status : unpaid
     * datetime : 1493263270
     * service_type : writing
     * paper_type : Essay
     * subject : Accounting
     * name : Luan van tot nghiep co khi
     * citation_style : MLA
     * instructions : test android mobile app
     * academic_level : college
     * num_pages : 7
     * spacing : double
     * revisions_left : 1
     * deadline : 120
     * due_in : 431971
     * due_in_text : 4d 23h 59m
     * price : 69.93
     * coupon : {"code":null,"value":0}
     * posts : [{"id":3245,"datetime":"1493263271","author":"vutnq","content":"test android mobile app","has_attachment":false,"attachment_file_name":null,"attachment_file_size":0}]
     */

    private int id;
    private String status;
    private String datetime;
    private String service_type;
    private String paper_type;
    private String subject;
    private String name;
    private String citation_style;
    private String instructions;
    private String academic_level;
    private int num_pages;
    private String spacing;
    private int revisions_left;
    private int deadline;
    private String due_in;
    private String due_in_text;
    private String price;
    private CouponBean coupon;
    private List<PostsBean> posts;

    @SerializedName("paypal_info")
    private PaypalInfo paypalInfo;

    public PaypalInfo getPaypalInfo() {
        return this.paypalInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status.substring(0, 1).toUpperCase() + status.substring(1);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getService_type() {
        return service_type.substring(0, 1).toUpperCase() + service_type.substring(1);
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getPaper_type() {
        return paper_type;
    }

    public void setPaper_type(String paper_type) {
        this.paper_type = paper_type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCitation_style() {
        return citation_style.substring(0, 1).toUpperCase() + citation_style.substring(1);
    }

    public void setCitation_style(String citation_style) {
        this.citation_style = citation_style;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getAcademic_level() {
        return academic_level.substring(0, 1).toUpperCase() + academic_level.substring(1);
    }

    public void setAcademic_level(String academic_level) {
        this.academic_level = academic_level;
    }

    public int getNum_pages() {
        return num_pages;
    }

    public void setNum_pages(int num_pages) {
        this.num_pages = num_pages;
    }

    public String getSpacing() {
        return spacing.substring(0, 1).toUpperCase() + spacing.substring(1);
    }

    public void setSpacing(String spacing) {
        this.spacing = spacing;
    }

    public int getRevisions_left() {
        return revisions_left;
    }

    public void setRevisions_left(int revisions_left) {
        this.revisions_left = revisions_left;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public String getDue_in() {
        return due_in;
    }

    public void setDue_in(String due_in) {
        this.due_in = due_in;
    }

    public String getDue_in_text() {
        return due_in_text;
    }

    public void setDue_in_text(String due_in_text) {
        this.due_in_text = due_in_text;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public CouponBean getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponBean coupon) {
        this.coupon = coupon;
    }

    public List<PostsBean> getPosts() {
        return posts;
    }

    public void setPosts(List<PostsBean> posts) {
        this.posts = posts;
    }

    public static class CouponBean implements Serializable {
        /**
         * code : null
         * value : 0
         */

        private Object code;
        private int value;

        public Object getCode() {
            return code;
        }

        public void setCode(Object code) {
            this.code = code;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class PostsBean implements Serializable {
        /**
         * id : 3245
         * datetime : 1493263271
         * author : vutnq
         * content : test android mobile app
         * has_attachment : false
         * attachment_file_name : null
         * attachment_file_size : 0
         */

        private int id;
        private int author_id;
        private String datetime;
        private String author;
        private String content;
        private boolean has_attachment;
        private Object attachment_file_name;
        private int attachment_file_size;

        public int getId() {
            return id;
        }

        public int getAuthorId() {
            return author_id;
        }

        public void setAuthorId(int author_id) {
            this.author_id = author_id;
        }

        public String getDatetime() {
            return datetime;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean checkHasAttachment() {
            return has_attachment;
        }

        public void setHasAttachment(boolean has_attachment) {
            this.has_attachment = has_attachment;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public Object getAttachment_file_name() {
            return attachment_file_name;
        }

        public void setAttachment_file_name(Object attachment_file_name) {
            this.attachment_file_name = attachment_file_name;
        }

        public int getAttachment_file_size() {
            return attachment_file_size;
        }

        public void setAttachment_file_size(int attachment_file_size) {
            this.attachment_file_size = attachment_file_size;
        }
    }
}

