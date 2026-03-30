<meta name="layout" content="main"/>

<<<<<<< HEAD
=======
<!-- Header -->
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
<div class="d-flex justify-content-between align-items-start mb-4">
    <div>
        <div class="page-title">Book Categories</div>
        <div class="page-subtitle">Organize your collection by genres and topics.</div>
    </div>
</div>

<<<<<<< HEAD
<div class="card-white mb-4">
    <g:form action="save" method="post">
        <div class="d-flex gap-3 flex-wrap">
            <input type="text" name="name"
                   class="input-custom flex-grow-1"
                   style="min-width: 200px;"
=======
<!-- Add Category Form -->
<div class="card-white">
    <g:form action="save" method="post">
        <div class="d-flex gap-3">
            <input type="text" name="name"
                   class="input-custom"
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
                   placeholder="Enter new category name..."
                   required/>
            <button type="submit" class="btn-green" style="white-space: nowrap">
                Add Category
            </button>
        </div>
    </g:form>
</div>

<<<<<<< HEAD
<div class="card-white p-0 overflow-hidden">
    <table class="categories-table w-100">
        <thead>
        <tr>
            <th>Category Name</th>
            <th>Books</th>
            <th class="text-end">Actions</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${categories}" var="cat">
            <g:set var="catNameAttr" value="${(cat.categoryName ?: '').replaceAll(/\s+/, ' ').trim()}"/>
            <tr>
                <td>
                    <g:link controller="category" action="show" id="${cat.id}" class="text-decoration-none category-table-link">
                        ${cat.categoryName}
                    </g:link>
                </td>
                <td>${cat.books?.size() ?: 0}</td>
                <td class="text-end">
                    <div class="d-flex gap-2 justify-content-end">
                        <button type="button" class="btn-icon-edit btn-edit-category"
                                data-bs-toggle="modal"
                                data-bs-target="#editCategoryModal"
                                data-category-id="${cat.id}"
                                data-category-name="${catNameAttr}"
                                title="Edit category">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <g:if test="${!cat.books}">
                            <g:form action="delete" method="post" style="display:inline" class="archive-confirm-form"
                                    data-archive-title="Delete this category?"
                                    data-archive-text="This category has no books and will be removed permanently."
                                    data-confirm-label="Yes, delete">
                                <input type="hidden" name="id" value="${cat.id}"/>
                                <button type="submit" class="btn-icon-delete" title="Delete category">
                                    <i class="bi bi-trash3"></i>
                                </button>
                            </g:form>
                        </g:if>
                    </div>
                </td>
            </tr>
        </g:each>
        <g:if test="${!categories}">
            <tr>
                <td colspan="3" class="text-center py-5 text-muted">
                    <i class="bi bi-tag" style="font-size:40px;display:block;margin-bottom:12px"></i>
                    No categories yet — add one above.
                </td>
            </tr>
        </g:if>
        </tbody>
    </table>
</div>

<g:render template="modals"/>
=======
<!-- Categories Grid -->
<div class="categories-grid">
    <g:each in="${categories}" var="cat">
        <div class="category-card">
            <div class="d-flex justify-content-between align-items-start">
                <g:link controller="category"
                        action="show"
                        id="${cat.id}"
                        class="text-decoration-none flex-grow-1">
                    <div>
                        <div class="category-card-name">${cat.categoryName}</div>
                        <div class="category-card-count">
                            ${cat.books?.size() ?: 0} BOOKS
                        </div>
                    </div>
                </g:link>


                <g:if test="${!cat.books}">
                    <g:form action="delete" method="post" style="display:inline">
                        <input type="hidden" name="id" value="${cat.id}"/>
                        <button type="submit" class="btn-delete">
                            <i class="bi bi-trash3"></i>
                        </button>
                    </g:form>
                </g:if>
            </div>
        </div>
    </g:each>
</div>
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
