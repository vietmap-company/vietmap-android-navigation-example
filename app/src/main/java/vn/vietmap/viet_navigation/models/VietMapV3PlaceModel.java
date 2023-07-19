package vn.vietmap.viet_navigation.models;

public class VietMapV3PlaceModel {
        private String display;
        private String name;
        private String hs_num;
        private String street;
        private String address;
        private int city_id;
        private String city;
        private int district_id;
        private String district;
        private int ward_id;
        private String ward;
        private double lat;
        private double lng;

        // Getters and Setters

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHs_num() {
            return hs_num;
        }

        public void setHs_num(String hs_num) {
            this.hs_num = hs_num;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getCity_id() {
            return city_id;
        }

        public void setCity_id(int city_id) {
            this.city_id = city_id;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getDistrict_id() {
            return district_id;
        }

        public void setDistrict_id(int district_id) {
            this.district_id = district_id;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public int getWard_id() {
            return ward_id;
        }

        public void setWard_id(int ward_id) {
            this.ward_id = ward_id;
        }

        public String getWard() {
            return ward;
        }

        public void setWard(String ward) {
            this.ward = ward;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

}
