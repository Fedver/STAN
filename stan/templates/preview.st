preview(breadcrumb, notifications, header, separator, delimiter, hasheader, tablename) ::= <<
	$header()$
	$breadcrumbs(breadcrumb)$
	
	$alerts(notifications)$
	
	<div id="alert-message">
	</div>
	
	<form name="preview" action="/update-preview-table" method="post">
		<ul class="nav">
		<div class="form-group">
			<label for="separator" class="col-sm-2 control-label">Separator</label>
			<div class="col-sm-4">
			  <input type="text" class="form-control" id="separator" placeholder="column1,column2,column3" name="separator" value="$separator$">
			</div>
			<label for="delimiter" class="col-sm-2 control-label">Text Delimiter</label>
			<div class="col-sm-4">
			  <input type="text" class="form-control" id="delimiter" placeholder='"value","value, containing, the, separator"' name="delimiter" value="$delimiter$">
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-12">
			  <div class="checkbox">
				<label>
				  <input type="checkbox" name="header" $hasheader$> <b>Has header</b>
				</label>
			  </div>
			</div>
		</div>
		<div class="pull-left">
			<input type="hidden" id="tablename" name="tablename" value="$tablename$">
			<button type="submit" class="btn btn-primary" id="refresh" name="refresh">Refresh</button>
			<button type="submit" class="btn btn-primary" id="confirm" name="confirm" formaction="/annotation">Confirm</button>
		</div>
		</ul>
	</form>
	<br />
	
	<table data-classes="table table-hover table-condensed" 
		   data-height="300" 
		   data-striped="true" 
		   data-toggle="table"
		   data-pagination="true"
		   data-side-pagination="server"
		   data-page-size="100"
		   data-url="/preview/rows"
		   id="table">
	    <thead>
			<tr>
				$header:{ h |
	    			<th data-field="$i$">
	    				<span id="header-$i$" name="header-$i$" object="$h.object$" property-type="$h.propertyType$" class="$h.styleClass$" title="$h.uri$">$h.label$</span>
	    			</th>
	    		}$
    		</tr>
	    </thead>
	</table>
	
	$footer()$
>>