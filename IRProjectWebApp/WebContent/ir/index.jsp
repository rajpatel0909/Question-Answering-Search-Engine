<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <title>"Information Retrieval"</title>
    <meta name="generator" content="Bootply" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/styles.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<style>
    .logo-small {
        color: #86aef4;
        font-size: 20px;
    }
    .logo-star {
        color: #f4ee14;
        font-size: 20px;
    }
	.imgContainer{
    float:left;
}

.carousel-control.right, .carousel-control.left {
    background-image: none;
    color: #f4511e;
}

.carousel-indicators li {
    border-color: #f4511e;
}

.carousel-indicators li.active {
    background-color: #f4511e;
}

.item h4 {
    font-size: 19px;
    line-height: 1.375em;
    font-weight: 400;
    font-style: italic;
    margin: 50px 0;
}

.item span {
    font-style: normal;
}

.centered {
    display: block;
    margin-left: auto;
    margin-right: auto;
}
.loader {
  border: 5px solid #f3f3f3;
  border-radius: 50%;
  border-top: 5px solid #4EF84E;
  width: 50px;
  height: 50px;
  -webkit-animation: spin 2s linear infinite;
  animation: spin 2s linear infinite;
}
@-webkit-keyframes spin {
  0% { -webkit-transform: rotate(0deg); }
  100% { -webkit-transform: rotate(360deg); }
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
<div style="    position: absolute;    top: 0%;    right: 0%;">
<!-- <img src="IEye.png" style=" height: 50px;" title="About Us"> -->
<a class="text-info text-right vertical-align" href="aboutUs.jsp" ><h4 style="margin-right:15px !important">Help</h4></a>
</div>
<div class="container">
  <br>
  <!--<div id="myCarousel" class="carousel slide text-center" data-ride="carousel">

    <ol class="carousel-indicators">
      <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
      <li data-target="#myCarousel" data-slide-to="1"></li>
      <li data-target="#myCarousel" data-slide-to="2"></li>
      <li data-target="#myCarousel" data-slide-to="3"></li>
    </ol>


    <div class="carousel-inner" role="listbox">

      <div class="item active">
        <span style="font-style:normal;">Don't commit on master when drunk</span>
      </div>
    
      <div class="item">
        <span style="font-style:normal;">Lets Code and break everything!!</span>
      </div>

      <div class="item">
        <span style="font-style:normal;">A clever person solves a problem.
A wise person avoids it.</span>
      </div>
	  <div class="item">
        <span style="font-style:normal;">No matter what the problem is,
it's always a people problem.</span>
      </div>
    </div>


    <a class="left carousel-control" href="#myCarousel" role="button" data-slide="prev">
      <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
      <span class="sr-only">Previous</span>
    </a>
    <a class="right carousel-control" href="#myCarousel" role="button" data-slide="next">
      <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
      <span class="sr-only">Next</span>
    </a>
  </div>
</div>
<br /><br /><br /><br />-->

<div class="conatainer-fluid">
<div class="row">
	<div class="col-sm-3"></div>
	<div class="col-sm-6">
      <a href="index.jsp">
        <img  class="centered"  src="illuminati_text.jpg" style="width:50%;">
      </a>
	  </div>
	<div class="col-sm-3"></div>
	</div>
    <div class="row">
	<div class="col-sm-3"></div>
	<div class="col-sm-6">
        <form action="../Servlet" class="navbar-form" role="search">
          <div id="search_text" class="input-group" style="width:100%">
            <!-- <form action="MyServlet" class="navbar-form" role="search"> -->
			  <input list="questionType" name="questionType" type="text" class="form-control input-lg" placeholder="Search">
			  <datalist id="questionType">
				<!-- <option value="Who is A">
				<option value="Who is b">
				<option value="Who is c">
				<option value="Who is d"> -->
				<option value="Who is Governor of RBI">
				<option value="Who is PM of India">
				<option value="Who is Prime Minister of India">
				<option value="Who is HM of India">
				<option value="Who is Home Minister of Indi">
				<option value="Who is CM of Delhi">
				<option value="Who implemented demonetisation">
				<option value="Who is CM of west bengal">
				<option value="Who is Chief Minister of west bengal">
				<option value="Who is in support of demonetisation">
				<option value="When was demonetisation implemented">
				<option value="What are after effects of demonetization">
				<option value="What are all the policies of demonetization">
				<option value="Where was the demonetization policy announced">
				<option value="How many people died due to demonetisation">
				<option value="How many notes were exchanged due to demonetization">
				<option value="why was demonetisation implemented">
				<option value="Who is against demonetisation">
			<!-- 	<option value="What">
				<option value="Where">
				<option value="Why"> -->
			  </datalist>
  

<br /><br /><br />
<input id="submit" class="btn btn-default centered" type="submit" value="Search" onclick="showDiv()"></input>
<div id="loaderDiv" style="display:none;" class="loader centered"></div>
</div>
</form>
</div>
<div class="col-sm-3"></div>
<div class="row"></div>
<footer class="footer" style="position:absolute; bottom:0% !important;">
<div class="container">
<div class="col-sm-1"></div>
<div class="col-sm-10">
<div class="text-center">
<p class="text-muted">Team Illuminati: <a target="blank" href="https://www.linkedin.com/in/anand-prashant-popat-43949511a">Anand Popat</a>, <a target="blank" href="https://www.linkedin.com/in/mishra10divya">Divya Mishra</a>, <a target="blank" href="https://www.linkedin.com/in/nitikachaudhary">Nitika Chaudhary</a>, <a target="blank" href="Divya Mishra (divyamis@buffalo.edu, divyamis@g-mail.buffalo.edu)
https://www.linkedin.com/in/rajpatel0909">Raj Patel</a></div>
</div>
<div class="col-sm-1"></div>
</div>
</footer>
</body>
<script language="javascript" type="text/javascript">
function showDiv() {
	   document.getElementById('loaderDiv').style.display = "block";
	   document.getElementById('submit').style.display = "none";
	}
</script>
</html>
