package alCarmel

class CategoryController {

    CategoryService categoryService
    SecurityService securityService

    // Categories listing page.
    def index() {
        if(!securityService.hasRole(session ,"ADMIN"))
        {
            redirect(controller: 'auth', action: 'login')
            return
        }
        [categories: categoryService.getCategories()]
    }

    // Shows all books that belong to a single category, rendered
    // using the same card layout as the main books page.
    def show(Long id) {
        if(!securityService.hasRole(session ,"ADMIN"))
        {
            redirect(controller: 'auth', action: 'login')
            return
        }
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
        if(!securityService.hasRole(session ,"ADMIN"))
        {
            redirect(controller: 'auth', action: 'login')
            return
        }
        def category = categoryService.saveCategory(params)
        if (category.hasErrors()) {
            flash.error = 'Category already exists'
            redirect(action: 'index')
        } else {
            flash.success = 'Category added successfully'
            redirect(action: 'index')

        }
    }

<<<<<<< HEAD
    def update() {
        if (!securityService.hasRole(session, 'ADMIN')) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        def category = categoryService.updateCategory(params.long('id'), params)
        if (!category) {
            flash.error = 'Category not found'
            redirect(action: 'index')
            return
        }
        if (category.hasErrors()) {
            flash.error = 'Category already exists or name is invalid'
            redirect(action: 'index')
        } else {
            flash.success = 'Category updated successfully'
            redirect(action: 'index')
        }
    }

=======
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
    // Deletes a category that has no books.
    def delete() {
        if(!securityService.hasRole(session ,"ADMIN"))
        {
            redirect(controller: 'auth', action: 'login')
            return
        }
        categoryService.deleteCategory(params.id as Long)
        flash.success = 'Category deleted successfully'
        redirect(action: 'index')
    }
}