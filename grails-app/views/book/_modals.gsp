%{-- 
  Book add / edit modals.
  Extracted from `index.gsp` so the page layout remains focused
  on the list, and the form markup is reusable/maintainable.
--}%
<span id="book-page-api-config" class="visually-hidden" aria-hidden="true"
      data-book-edit-base="${createLink(controller: 'book', action: 'edit')}"></span>

<!-- ===== ADD MODAL ===== -->
<div class="modal fade" id="addBookModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content modal-custom">

            <div class="modal-header-custom">
                <h5>Add New Book</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <g:form action="save" method="post">
                <div class="modal-body-custom">

                    <div class="form-group-custom">
                        <label>BOOK TITLE</label>
                        <input type="text" name="bookTitle" class="input-custom"
                               placeholder="Enter book title..." required/>
                    </div>

                    <div class="form-group-custom">
                        <label>AUTHOR</label>
                        <input type="text" name="authorName" class="input-custom"
                               placeholder="Enter author name..." required/>
                    </div>

                    <div class="form-group-custom">
                        <label>DESCRIPTION</label>
                        <textarea name="description" class="input-custom"
                                  rows="3" placeholder="Enter description..."></textarea>
                    </div>

                    <div class="row">
                        <div class="col-6">
                            <div class="form-group-custom">
                                <label>TOTAL COPIES</label>
                                <input type="number" name="totalCopies"
                                       class="input-custom" value="1" min="1" required/>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group-custom">
                                <label>PUBLISH YEAR</label>
                                <%
                                    int currentYear = java.util.Calendar.instance.get(java.util.Calendar.YEAR)
                                %>
                                <input type="number"
                                       name="publishYear"
                                       class="input-custom"
                                       placeholder="2002"
                                       required
                                       min="1700"
                                       max="${currentYear}"/>
                            </div>
                        </div>
                    </div>

                    <div class="form-group-custom">
                        <label>CATEGORY</label>
                        <select required name="categoryId" id="addCategoryId" class="input-custom">
                            <option value="">Select category...</option>
                            <g:each in="${categories}" var="cat">
                                <option value="${cat.id}">${cat.categoryName}</option>
                            </g:each>
                        </select>
                    </div>

                </div>

                <div class="modal-footer-custom">
                    <button type="button" class="btn-cancel"
                            data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn-green">Save Book</button>
                </div>
            </g:form>

        </div>
    </div>
</div>

<!-- ===== EDIT MODAL ===== -->
<div class="modal fade" id="editBookModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content modal-custom">

            <div class="modal-header-custom">
                <h5>Edit Book</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <g:form action="update" method="post">
                <input type="hidden" name="id" id="editBookId"/>

                <div class="modal-body-custom">

                    <div class="form-group-custom">
                        <label>BOOK TITLE</label>
                        <input type="text" name="bookTitle" id="editTitle"
                               class="input-custom" required/>
                    </div>

                    <div class="form-group-custom">
                        <label>AUTHOR</label>
                        <input type="text" name="authorName" id="editAuthor"
                               class="input-custom" required/>
                    </div>

                    <div class="form-group-custom">
                        <label>DESCRIPTION</label>
                        <textarea name="description" id="editDescription"
                                  class="input-custom" rows="3"></textarea>
                    </div>

                    <div class="row">
                        <div class="col-6">
                            <div class="form-group-custom">
                                <label>TOTAL COPIES</label>
                                <input type="number" name="totalCopies"
                                       id="editTotalCopies" class="input-custom" min="1" required/>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group-custom">
                                <label>PUBLISH YEAR</label>
                                <%
                                    int currentYearEdit = java.util.Calendar.instance.get(java.util.Calendar.YEAR)
                                %>
                                <input type="number"
                                       name="publishYear"
                                       id="editPublishYear"
                                       class="input-custom"
                                       required
                                       min="1500"
                                       max="${currentYearEdit}"/>
                            </div>
                        </div>
                    </div>

                    <div class="form-group-custom">
                        <label>CATEGORY</label>
                        <select required name="categoryId" id="editCategoryId" class="input-custom">
                            <option value="">Select category...</option>
                            <g:each in="${categories}" var="cat">
                                <option value="${cat.id}">${cat.categoryName}</option>
                            </g:each>
                        </select>
                    </div>

                </div>

                <div class="modal-footer-custom">
                    <button type="button" class="btn-cancel"
                            data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn-green">Update Book</button>
                </div>
            </g:form>

        </div>
    </div>
</div>

