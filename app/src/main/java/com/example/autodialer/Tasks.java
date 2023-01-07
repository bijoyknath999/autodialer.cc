package com.example.autodialer;

public class Tasks {
    public String id;
    public String name;
    public String country;
    public String city;
    public String phone;
    public String date_time;

public Tasks(String id,String name,String country,String city,String phone){
    this.id=id;
    this.name=name;
    this.country=country;
    this.city=city;
    this.phone=phone;
}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getDate_time() {
        return date_time;
    }

    @Override
    public String toString() {
        return "Tasks{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", phone='" + phone + '\'' +
                ", date_time='" + date_time + '\'' +
                '}';
    }
}
