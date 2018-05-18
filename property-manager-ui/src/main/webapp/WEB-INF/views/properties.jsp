<html>
 <head>
  <title>Live Add Edit Delete Datatables Records using PHP Ajax</title>
  <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" />
  <script src="https://cdn.datatables.net/1.10.15/js/jquery.dataTables.min.js"></script>
  <script src="https://cdn.datatables.net/1.10.15/js/dataTables.bootstrap.min.js"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/css/bootstrap-datepicker.css" />
  <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/js/bootstrap-datepicker.js"></script>
  <style>
  body
  {
   margin:0;
   padding:0;
   background-color:#f1f1f1;
  }
  .box
  {
   width:1270px;
   padding:20px;
   background-color:#fff;
   border:1px solid #ccc;
   border-radius:5px;
   margin-top:25px;
   box-sizing:border-box;
  }
  </style>
 </head>
 <body>
  <div class="container box">
   <h1 align="center">Property Manager</h1>
   <br />
   <div class="table-responsive">
   <br />
    <div align="right">
     <button type="button" name="add" id="add" class="btn btn-info">Add</button>
    </div>
    <br />
    <div id="alert_message"></div>
    <table id=property_display class="table table-bordered table-striped">
     <thead>
      <tr>
       <th>Address</th>
       <th>Postcode</th>
       <th>Location</th>
       <th>Surface</th>
       <th>Bedrooms</th>
       <th></th>
      </tr>
     </thead>
     <tbody>
     </tbody>
    </table>
   </div>
  </div>
 </body>
</html>

<script type="text/javascript" language="javascript" >
 $(document).ready(function(){
  
  fetch_data();

  function fetch_data()
  {  
	  
	   $('#property_display tr').remove();
//    var dataTable = $('#user_data').DataTable(data);
$.getJSON( "listProperties", function( data ) {
	$.each(data, function(id, property) {
		$('#property_display tbody').append('<tr>');
		appendModifiableEntry(id, 'address', property.address);
		appendModifiableEntry(id, 'postcode', property.postcode);
		appendModifiableEntry(id, 'location', property.location);
		appendModifiableEntry(id, 'surface', property.surface);
		appendModifiableEntry(id, 'bedrooms', property.bedrooms);
		$('#property_display tbody tr').last().append('<td><button type="button" name="delete" class="btn btn-danger btn-xs delete" id="'+id+'">Delete</button></td>');

	});
});
 
function appendModifiableEntry(id, columnName, columnValue) {
	$('#property_display tbody tr').last().append('<td>'+
			'<div contenteditable class="update" data-id="'+id+'" data-column="'+columnName+'">'+
			columnValue
			+'</div></td>');
}

$('property_display').DataTable();

  }
  
  function update_data(id, column_name, value)
  {
   $.ajax({
    url:"update.php",
    method:"POST",
    data:{id:id, column_name:column_name, value:value},
    success:function(data)
    {
     $('#alert_message').html('<div class="alert alert-success">'+data+'</div>');
     fetch_data();
    }
   });
   setInterval(function(){
    $('#alert_message').html('');
   }, 5000);
  }

  $(document).on('blur', '.update', function(){
   var id = $(this).data("id");
   var column_name = $(this).data("column");
   var value = $(this).text();
   update_data(id, column_name, value);
  });
  
  $('#add').click(function(){
   var html = '<tr>';
   html += '<td contenteditable id="address"></td>';
   html += '<td contenteditable id="postcode"></td>';
   html += '<td contenteditable id="location"></td>';
   html += '<td contenteditable id="surface"></td>';
   html += '<td contenteditable id="bedrooms"></td>';
   html += '<td><button type="button" name="insert" id="insert" class="btn btn-success btn-xs">Insert</button></td>';
   html += '</tr>';
   $('#property_display tbody').prepend(html);
  });
  
  $(document).on('click', '#insert', function(){
   var property = {};
   property['address'] = $('#address').text();
   property['postcode'] = $('#postcode').text();
   property['location'] = $('#location').text();
   property['surface'] = $('#surface').text();
   property['bedrooms'] = $('#bedrooms').text();
   

   $.ajax({  
       type : 'POST',   
       contentType: 'application/json; charset=utf-8',
       url : 'property',
       dataType: 'json',
       data : JSON.stringify(property),
       success : function(response) {  
    	   $('#alert_message').html('<div class="alert alert-success">'+property+'</div>');
    	   fetch_data();
       },  
       error : function(e) {  
    	   setInterval(function(){
    		     $('#alert_message').html('');
    		    }, 5000);  
          }  
         });  
   
  });
  
  $(document).on('click', '.delete', function(){
   var id = $(this).attr("id");
   if(confirm("Are you sure you want to remove this?"))
   {
    $.ajax({
     url:"property/remove",
     method:"POST",
     data:{id:id},
     success:function(data){
      $('#alert_message').html('<div class="alert alert-success">'+data+'</div>');
      fetch_data();
     }
    });
    setInterval(function(){
     $('#alert_message').html('');
    }, 5000);
   }
  });
 });
</script>	