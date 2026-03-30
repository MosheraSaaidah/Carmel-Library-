package alCarmel

class Category {
    String categoryName
    static  hasMany = [books:Book]

    static constraints = {
        categoryName blank: false ,unique: true
    }

}
