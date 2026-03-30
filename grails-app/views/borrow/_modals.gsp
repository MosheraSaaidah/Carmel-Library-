%{--
  Borrowing "New borrow" modal.
  Extracted from `index.gsp` to separate the add‑borrow form from
  the main table layout and make it easier to reuse.
--}%

<!-- ===== ADD BORROW MODAL ===== -->
<div class="modal fade" id="addBorrowModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content modal-custom">

            <div class="modal-header-custom">
                <h5>New Book borrow</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <g:form action="save" method="post">
                <div class="modal-body-custom">

                    <div class="form-group-custom">
                        <label>SELECT MEMBER</label>
                        <select name="memberId" class="input-custom" required>
                            <option value="">Choose a member...</option>
                            <g:each in="${members}" var="member">
                                <option value="${member.id}">${member.fullName}</option>
                            </g:each>
                        </select>
                    </div>

                    <div class="form-group-custom">
                        <label>SELECT BOOK</label>
                        <select name="bookId" class="input-custom" required>
                            <option value="">Choose a book...</option>
                            <g:each in="${books}" var="book">
                                <option value="${book.id}">
                                    ${book.bookTitle} (${book.availableCopies} available)
%{--                                    --}%
                                </option>
                            </g:each>
                        </select>
                    </div>

                    <div class="policy-box">
                        <i class="bi bi-calendar-check"></i>
                        <div class="policy-box-content">
                            <div class="policy-title">14-Day Policy</div>
                            <div class="policy-text">The due date will be automatically set to 14 days from today.</div>
                        </div>
                    </div>

                </div>

                <div class="modal-footer-custom">
                    <button type="button" class="btn-cancel"
                            data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn-green">Confirm Loan</button>
                </div>
            </g:form>

        </div>
    </div>
</div>

