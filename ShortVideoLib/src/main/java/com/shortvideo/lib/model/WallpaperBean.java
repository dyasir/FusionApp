package com.shortvideo.lib.model;

import java.util.List;

public class WallpaperBean {

    private List<ImagesDTO> images;

    public List<ImagesDTO> getImages() {
        return images;
    }

    public void setImages(List<ImagesDTO> images) {
        this.images = images;
    }

    public static class ImagesDTO {
        private Double addTime;
        private List<String> cids;
        private Integer collect;
        private String id;
        private String img;
        private String origin;
        private Integer rank;
        private String steam;
        private List<String> tags;
        private String uplay;

        public Double getAddTime() {
            return addTime;
        }

        public void setAddTime(Double addTime) {
            this.addTime = addTime;
        }

        public List<String> getCids() {
            return cids;
        }

        public void setCids(List<String> cids) {
            this.cids = cids;
        }

        public Integer getCollect() {
            return collect;
        }

        public void setCollect(Integer collect) {
            this.collect = collect;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public String getSteam() {
            return steam;
        }

        public void setSteam(String steam) {
            this.steam = steam;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getUplay() {
            return uplay;
        }

        public void setUplay(String uplay) {
            this.uplay = uplay;
        }
    }
}
