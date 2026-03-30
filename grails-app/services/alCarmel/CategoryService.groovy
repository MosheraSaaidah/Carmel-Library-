package alCarmel

import grails.gorm.transactions.Transactional

@Transactional
class CategoryService {

    def getCategories() {
        Category.list(sort: 'categoryName', order: 'asc')
    }
    def saveCategory(Map params) {
      
        def rawName   = params.name ?: params.categoryName
        def category  = new Category(categoryName: rawName?.trim())
        category.save()
        category
    }
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

    def deleteCategory(Long id) {
        Category.get(id)?.delete()
    }
}