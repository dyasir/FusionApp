package com.shortvideo.lib.model;

import java.io.Serializable;
import java.util.List;

public class HomeBean implements Serializable {

    private Integer pageNo;
    private Integer pageSize;
    private Integer totalRecord;
    private Integer totalPage;
    private Long dataTime;
    private List<DataDTO> data;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Long getDataTime() {
        return dataTime;
    }

    public void setDataTime(Long dataTime) {
        this.dataTime = dataTime;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public static class DataDTO implements Serializable {
        private Integer type;
        private VideoDTO video;
        private BannerDTO banner;

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public VideoDTO getVideo() {
            return video;
        }

        public void setVideo(VideoDTO video) {
            this.video = video;
        }

        public BannerDTO getBanner() {
            return banner;
        }

        public void setBanner(BannerDTO banner) {
            this.banner = banner;
        }

        public static class VideoDTO implements Serializable {
            private Integer id;
            private String url;
            private String thumburl;
            private String title;
            private String suffix;
            private Integer like_count;
            private String desc;
            private String author_id;
            private String author;
            private boolean is_like;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getThumburl() {
                return thumburl;
            }

            public void setThumburl(String thumburl) {
                this.thumburl = thumburl;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getSuffix() {
                return suffix;
            }

            public void setSuffix(String suffix) {
                this.suffix = suffix;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public String getAuthor_id() {
                return author_id;
            }

            public void setAuthor_id(String author_id) {
                this.author_id = author_id;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public Integer getLike_count() {
                return like_count;
            }

            public void setLike_count(Integer like_count) {
                this.like_count = like_count;
            }

            public boolean isIs_like() {
                return is_like;
            }

            public void setIs_like(boolean is_like) {
                this.is_like = is_like;
            }
        }

        public static class BannerDTO implements Serializable {
            private Integer id;
            private String url;
            private String link;
            private String title;
            private String icon;
            private Integer banner_type;
            private String desc;
            private boolean isShow;
            private boolean adShow;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public Integer getBanner_type() {
                return banner_type;
            }

            public void setBanner_type(Integer banner_type) {
                this.banner_type = banner_type;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public boolean isShow() {
                return isShow;
            }

            public void setShow(boolean show) {
                isShow = show;
            }

            public boolean isAdShow() {
                return adShow;
            }

            public void setAdShow(boolean adShow) {
                this.adShow = adShow;
            }
        }
    }
}
