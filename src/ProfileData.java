public class ProfileData {
    private String name;
    private String gender;
    private int age;
    private String email;
    private String instagram;
    private String linkedin;

    public ProfileData() {
        this("", "", 0, "", "", "");
    }

    public ProfileData(String name, String gender, int age, String email, 
            String instagram, String linkedin) {
        this.name = name != null ? name : "";
        this.gender = gender != null ? gender : "";
        this.age = age;
        this.email = email != null ? email : "";
        this.instagram = instagram != null ? instagram : "";
        this.linkedin = linkedin != null ? linkedin : "";
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }
    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }
}