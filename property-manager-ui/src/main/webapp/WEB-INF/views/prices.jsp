<html>
 <head>
  <title>Property Manager - Property Prices</title>
  <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" />
  <script src="https://cdn.datatables.net/1.10.15/js/jquery.dataTables.min.js"></script>
  <script src="https://cdn.datatables.net/1.10.15/js/dataTables.bootstrap.min.js"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/css/bootstrap-datepicker.css" />
  <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/js/bootstrap-datepicker.js"></script>
 </head>
 <body>
  <div class="container box">
   <div class="table-responsive">
   <br />
    <div align="right">
     <button type="button" name="add" id="add" class="btn btn-info">Add</button>
    </div>
    <br />
    <div id="alert_message"></div>
    <table id=price_display class="table table-bordered table-striped">
     <thead>
      <tr>
       <th>Date</th>
       <th>Price</th>
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

function getParam(name){
		var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
		return results[1] || 0;
	}
 
 $(document).ready(function(){
  
  fetch_data();
  
  
  function clean_alert_message()
  { 
	  setInterval(function(){
		     $('#alert_message').html('');
		    }, 5000);  
  }

  function fetch_data()
  {  
	  
	   $('#price_display tbody tr').remove();

$.getJSON( "../property", { id : getParam('id') }, function( data ) {
	$.each(data.prices, function(id, price) {
		$('#price_display tbody').append('<tr>');
		$('#price_display tbody tr').last().append('<td>'+price.date+'</td>');
		$('#price_display tbody tr').last().append('<td>'+price.price+'</td>');
		

	});
});
 

$('price_display').DataTable();
  }
  
  $('#add').click(function(){
   var html = '<tr>';
   html += '<td><input data-provide="datepicker"  id="date"></td>';
   html += '<td contenteditable id="price"></td>';
   html += '<td><button type="button" name="insert" id="insert" class="btn btn-success btn-xs">Insert</button></td>';
   html += '</tr>';
   $('#price_display tbody').prepend(html);
   $('.datepicker').datepicker();
  });
  
  $(document).on('click', '#insert', function(){

	   var price = {};
	   price['date'] = $('#date').val();
	   price['price'] = $('#price').text();
	   var update = {};
	   update[getParam('id')] = price;
	  
   
   $.ajax({  
       type : 'POST',   
       contentType: 'application/json; charset=utf-8',
       url : 'price/add',
       data : JSON.stringify(update),
       success : function(response) {  
    	   $('#alert_message').html('<div class="alert alert-success">'+response+'</div>');
    	   fetch_data();
    	   clean_alert_message
       },  
       error : function(e) {
    	   $('#alert_message').html('<div class="alert alert-danger">Cannot add price</div>');
    	   fetch_data();
    	   clean_alert_message
          }  
         });  
   
  });
  
 });
</script>	