<link type="text/css" rel="stylesheet" href="/developer/css/int/form.css?@@TIMESTAMP@@" />
<script type="text/javascript" src="/developer/script/ext/jquery-1.11.0.min.js?@@TIMESTAMP@@"></script>
<script type="text/javascript" src="/developer/script/ext/jquery.i18n.properties-min-1.0.9.js?@@TIMESTAMP@@"></script>
<script type="text/javascript" src="/developer/script/int/admin.js?@@TIMESTAMP@@"></script>
<script type="text/javascript" src="/developer/script/int/serverSideValidation.js?@@TIMESTAMP@@"></script>
<script type="text/javascript" src="/developer/script/int/add.i18n.capability.js?@@TIMESTAMP@@"></script>

<div class="contentPanel">
    <div class="panelContent">
       <h1>Global Properties</h1>
    </div>
    <div class="panelContentFull">
        <div class="panelContent">
			<form id="adminForm" class="customForm">
				<div class="grid oneColumn" style="padding: 0px;">
					<ul id="serverErrorMessage" style="display: none">
					</ul>
				    <div class="formElementRow">
			        	<label id="ss_general_errors"></label>
			        </div>
					<div class="formElementRow">
						<label class="required" for="itemKey_label">Item Key</label>
						<input name="itemKey_input" type="text" id="itemKey_input_id" tabindex="1" />
					</div>
					<div class="formElementRow">
						<label class="required" for="field_key_label">Field Key</label>
						<input name="fieldKey_input" type="text" id="fieldKey_input_id" tabindex="2" />
					</div>
					<div class="formElementRow">
						<label class="required" for="description_label">Description</label>
						<textarea name="description_textArea" type="text" id="description_textArea_id" tabindex="3" rows="18" cols="100" style="width: 780px;"></textarea>
					</div>
					<div class="formElementRow">
						<input id="refresh_submit_id" type="submit" value="Refresh" />
					</div>					
				</div>
			</form>
		</div>
	</div>
</div>