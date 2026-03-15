<meta name="layout" content="main"/>

<!-- Header -->
<div class="d-flex justify-content-between align-items-start mb-4">
    <div>
        <div class="page-title">Book Categories</div>
        <div class="page-subtitle">Organize your collection by genres and topics.</div>
    </div>
</div>

<!-- Add Category Form -->
<div class="card-white">
    <g:form action="save" method="post">
        <div class="d-flex gap-3">
            <input type="text" name="name"
                   class="input-custom"
                   placeholder="Enter new category name..."
                   required/>
            <button type="submit" class="btn-green" style="white-space: nowrap">
                Add Category
            </button>
        </div>
    </g:form>
</div>

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