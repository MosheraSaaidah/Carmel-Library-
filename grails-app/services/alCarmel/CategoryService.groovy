package alCarmel

import grails.gorm.transactions.Transactional

@Transactional
class CategoryService {

<<<<<<< HEAD
    def getCategories() {
        Category.list(sort: 'categoryName', order: 'asc')
    }
    def saveCategory(Map params) {
      
=======
    // Returns categories ordered by name for the grid on the page.
    def getCategories() {
        Category.list(sort: 'categoryName', order: 'asc')
    }

    // Creates a new category. Any uniqueness/validation errors are
    // left on the returned instance for the controller to handle.
    def saveCategory(Map params) {
        // The form uses `name` as the field, so we map it into
        // the domain's `categoryName` property here.
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
        def rawName   = params.name ?: params.categoryName
        def category  = new Category(categoryName: rawName?.trim())
        category.save()
        category
    }
<<<<<<< HEAD
    def updateCategory(Long id, Map params) {
        def category = Category.get(id)
        if (!category) {
            return null
        }
        def rawName = params.name ?: params.categoryName
        category.categoryName = rawName?.trim()
        category.save()
        category
    }
=======
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e

    def deleteCategory(Long id) {
        Category.get(id)?.delete()
    }
}