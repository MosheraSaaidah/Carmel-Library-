package alCarmel

class User {
   
    String username
    String email
    String passwordHash
    String role
    Boolean enabled = false
    Boolean emailConfirmed = false
    String confirmationToken
    String confirmationCode

    Date dateCreated
    Date lastUpdated

    static constraints = {
        username       blank: false, unique: true
        email          blank: false, email: true, unique: true
        passwordHash     blank: false
        role             inList: ["MEMBER", "ADMIN"]
        confirmationToken nullable: true
        confirmationCode  nullable: true
    }

    static mapping = {
        table 'app_user'
        autoTimestamp true
    }
}