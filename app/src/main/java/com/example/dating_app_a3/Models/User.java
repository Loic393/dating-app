package com.example.dating_app_a3.Models;

import com.google.firebase.firestore.GeoPoint;
import com.google.android.gms.maps.model.LatLng;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String id;
    private String email;
    private String role;
    private String name;
    private String birthday;
    private int age;
    private String gender;
    private int height;
    private LatLng location;
    private String biography;
    private String preferredSex;
    private List<String> profilePictureUrls;
    private List<String> likes;
    private List<String> viewed;
    private List<String> matches;

    // Constructor for creating a user with minimal information
    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }

    // Constructor for creating a user with complete information
    public User(String id, String email, String role, String name, String birthday, String gender, int height, LatLng location,
                String biography, String preferredSex, List<String> profilePictureUrls, List<String> likes, List<String> viewed, List<String> matches) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.height = height;
        this.location = location;
        this.biography = biography;
        this.preferredSex = preferredSex;
        this.profilePictureUrls = profilePictureUrls;
        this.likes = likes;
        this.viewed = viewed;
        this.matches = matches;
    }

    // Method to convert the user object to a Map for Firebase
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("role", role);
        map.put("name", name);
        map.put("birthday", birthday);
        map.put("gender", gender != null ? gender : "unknown");
        map.put("height", height);
        GeoPoint geoPoint;
        if (location != null) {
            geoPoint = new GeoPoint(this.location.latitude, this.location.longitude);
        } else {
            // Set a default location if it's null
            geoPoint = new GeoPoint(0, 0);
        }
        map.put("location", geoPoint);
        map.put("biography", biography);
        map.put("preferredSex", preferredSex != null ? preferredSex : "unknown");
        map.put("profilePictureUrls" , profilePictureUrls);
        map.put("likes", likes);
        map.put("viewed", viewed);
        map.put("matches", matches);
        return map;
    }

    public static User toObject(Map<String, Object> data, String id) {
        User user = new User();
        user.setId(id);
        user.setEmail((String) data.get("email"));
        user.setRole((String) data.get("role"));
        user.setName((String) data.get("name"));

        user.setBirthday((String) data.get("birthday"));

        String birthday = (String) data.get("birthday");
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthdate = LocalDate.parse(birthday, formatter);
        Period age = Period.between(birthdate, currentDate);
        int ageInt = age.getYears();
        user.setAge(ageInt);

        user.setGender((String) data.get("gender"));

        // Check for null before attempting to access the value
        Object heightObj = data.get("height");
        user.setHeight(heightObj != null ? ((Long) heightObj).intValue() : 0);

        // Check for null before attempting to access the value
        Object latitudeObj = data.get("latitude");
        Object longitudeObj = data.get("longitude");
        user.setLocation((latitudeObj != null && longitudeObj != null) ? new LatLng((double) latitudeObj, (double) longitudeObj) : null);

        user.setBiography((String) data.get("biography"));
        user.setPreferredSex((String) data.get("preferredSex"));


        user.setProfilePictureUrls(convertObjectToList(data.get("profilePictureUrls")));
        user.setLikes(convertObjectToList(data.get("likes")));
        user.setViewed(convertObjectToList(data.get("viewed")));
        user.setMatches(convertObjectToList(data.get("matches")));

        return user;
    }

    @SuppressWarnings("unchecked")
    private static List<String> convertObjectToList(Object obj) {
        if (obj instanceof List) {
            return (List<String>) obj;
        }
        return null;
    }



    // Empty constructor required by Firebase
    public User() {
    }

    // Getter methods for accessing private fields
    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public int getHeight() {
        return height;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getBiography() {
        return biography;
    }

    public String getPreferredSex() {
        return preferredSex;
    }

    public List<String> getProfilePictureUrls() {
        return profilePictureUrls;
    }

    public List<String> getLikes() {
        return likes;
    }

    public List<String> getViewed() {
        return viewed;
    }

    public List<String> getMatches() {
        return matches;
    }

    public void setProfilePictureUrls(List<String> profilePictureUrls) {
        this.profilePictureUrls = profilePictureUrls;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public void setPreferredSex(String preferredSex) {
        this.preferredSex = preferredSex;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public void setViewed(List<String> viewed) {
        this.viewed = viewed;
    }

    public void setMatches(List<String> matches) {
        this.matches = matches;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
