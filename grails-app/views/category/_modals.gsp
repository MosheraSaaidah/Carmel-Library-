<div class="modal fade" id="editCategoryModal" tabindex="-1" aria-labelledby="editCategoryModalTitle" role="dialog">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content modal-custom">
            <div class="modal-header-custom">
                <h5 id="editCategoryModalTitle">Edit Category</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <g:form controller="category" action="update" method="post">
                <input type="hidden" name="id" id="editCategoryRecordId" value=""/>
                <div class="modal-body-custom">
                    <div class="form-group-custom">
                        <label>CATEGORY NAME</label>
                        <input type="text" name="name" id="editCategoryNameInput"
                               class="input-custom" required
                               placeholder="Category name..."/>
                    </div>
                </div>
                <div class="modal-footer-custom">
                    <button type="button" class="btn-cancel" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn-green">Update</button>
                </div>
            </g:form>
        </div>
    </div>
</div>
