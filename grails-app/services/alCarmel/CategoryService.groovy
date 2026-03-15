package alCarmel

import grails.gorm.transactions.Transactional

@Transactional
class CategoryService {

    // Returns categories ordered by name for the grid on the page.
    def getCategories() {
        Category.list(sort: 'categoryName', order: 'asc')
    }

    // Creates a new category. Any uniqueness/validation errors are
    // left on the returned instance for the controller to handle.
    def saveCategory(Map params) {
        // The form uses `name` as the field, so we map it into
        // the domain's `categoryName` property here.
        def rawName   = params.name ?: params.categoryName
        def category  = new Category(categoryName: rawName?.trim())
        category.save()
        category
    }

    def deleteCategory(Long id) {
        Category.get(id)?.delete()
    }
}