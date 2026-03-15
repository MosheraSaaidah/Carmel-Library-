package alCarmel

class CategoryController {

    CategoryService categoryService

    // Categories listing page.
    def index() {
        [categories: categoryService.getCategories()]
    }

    // Shows all books that belong to a single category, rendered
    // using the same card layout as the main books page.
    def show(Long id) {
        def category = Category.get(id)
        if (!category) {
            flash.error = 'Category not found'
            redirect(action: 'index')
            return
        }

        [
                category: category,
                books   : category.books
        ]
    }

    // Creates a category. If a category with the same name already
    // exists, we surface that as a flash error toast.
    def save() {
        def category = categoryService.saveCategory(params)
        if (category.hasErrors()) {
            flash.error = 'Category already exists'
            redirect(action: 'index')
        } else {
            flash.success = 'Category added successfully'
            redirect(action: 'index')

        }
    }

    // Deletes a category that has no books.
    def delete() {
        categoryService.deleteCategory(params.id as Long)
        flash.success = 'Category deleted successfully'
        redirect(action: 'index')
    }
}