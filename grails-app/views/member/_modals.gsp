%{--
  Member add / edit modals.
  Extracted from `index.gsp` to keep the table view clean and
  allow easier reuse or modification of the forms.
--}%

<%-- Centralized list of Palestinian cities for the member forms --%>
<%
    def cityOptions = [
            'Ramallah',
            'Nablus',
            'Hebron',
            'Bethlehem',
            'Jenin',
            'Tulkarm',
            'Qalqilya',
            'Jericho',
            'Salfit',
            'Tubas'
    ]
%>

<!-- ===== ADD MODAL ===== -->
<div class="modal fade" id="addMemberModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content modal-custom">

            <div class="modal-header-custom">
                <h5>Register New Member</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <g:form action="save" method="post">
                <div class="modal-body-custom">

                    <div class="form-group-custom">
                        <label>FULL NAME</label>
                        <input type="text" name="fullName"
                               class="input-custom"
                               placeholder="Enter full name..." required/>
                    </div>

                    <div class="form-group-custom">
                        <label>EMAIL</label>
                        <input type="email" name="email"
                               class="input-custom"
                               placeholder="Enter email..." required
                               inputmode="email"

                        />
                    </div>

                    <div class="form-group-custom">
                        <label>PHONE</label>
                        <input type="tel" name="phoneNumber"
                               class="input-custom"
                               placeholder=" (10–12 digits)"
                               required
                               inputmode="numeric"
                               pattern="[0-9]{10,12}"
                               maxlength="12"
                               title="10 to 12 digits only "
                               oninput="this.value=this.value.replace(/\D/g,'').slice(0,12)"/>
                    </div>

                    <div class="form-group-custom">
                        <label>CITY</label>
                        <g:select name="address"
                                  from="${cityOptions}"
                                  noSelection="['':'Select city']"
                                  class="input-custom"
                                  required="required"/>
                    </div>

                </div>

                <div class="modal-footer-custom">
                    <button type="button" class="btn-cancel"
                            data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn-green">Register</button>
                </div>
            </g:form>

        </div>
    </div>
</div>

<!-- ===== EDIT MODAL ===== -->
<div class="modal fade" id="editMemberModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content modal-custom">

            <div class="modal-header-custom">
                <h5>Edit Member</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <g:form action="update" method="post">
                <input type="hidden" name="id" id="editMemberId"/>

                <div class="modal-body-custom">

                    <div class="form-group-custom">
                        <label>FULL NAME</label>
                        <input type="text" name="fullName" id="editFullName"
                               class="input-custom" required/>
                    </div>

                    <div class="form-group-custom">
                        <label>EMAIL</label>
                        <input type="email" name="email" id="editEmail"
                               class="input-custom"
                               required
                               inputmode="email"
                        />
                    </div>

                    <div class="form-group-custom">
                        <label>PHONE</label>
                        <input type="tel" name="phoneNumber" id="editPhone"
                               class="input-custom"
                               required
                               inputmode="numeric"
                               pattern="[0-9]{10,12}"
                               maxlength="12"
                               title="10 to 12 digits only"
                               oninput="this.value=this.value.replace(/\D/g,'').slice(0,12)"/>
                    </div>

                    <div class="form-group-custom">
                        <label>CITY</label>
                        <g:select name="address" id="editAddress"
                                  from="${cityOptions}"
                                  noSelection="['':'Select city']"
                                  class="input-custom"
                                  required="required"/>
                    </div>

                </div>

                <div class="modal-footer-custom">
                    <button type="button" class="btn-cancel"
                            data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn-green">Update</button>
                </div>
            </g:form>

        </div>
    </div>
</div>

