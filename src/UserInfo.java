import java.util.List;

public class UserInfo {
    private String userName;
    private List<String> subjects;
    private List<String> groups;

    public UserInfo(String userName, List<String> subjects, List<String> groups) {
        this.userName = userName;
        this.subjects = subjects;
        this.groups = groups;
    }

    public String getUserName() {
        return userName;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public List<String> getGroups() {
        return groups;
    }
}
