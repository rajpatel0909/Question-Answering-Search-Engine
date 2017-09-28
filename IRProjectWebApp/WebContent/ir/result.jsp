<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<title>"Information Retrieval"</title>
<meta name="generator" content="Bootply" />
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<link href="ir/css/bootstrap.min.css" rel="stylesheet">
<link href="ir/css/styles.css" rel="stylesheet">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
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

.imgContainer {
	float: left;
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

.color-blue {
	color: #0000FF;
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

	<nav class="navbar navbar-default navbar-fixed-top">
	<div class="container-fluid">
		<!-- Brand and toggle get grouped for better mobile display -->
		<div class="navbar-header"></div>

		<!-- Collect the nav links, forms, and other content for toggling <a class="navbar-brand" href="index.html">
        <img  class="left"  src="illuminati_text.jpg" style="width:10%;">
      </a>-->
		<div class="collapse navbar-collapse"
			id="bs-example-navbar-collapse-1">
			<div class="nav navbar-nav navbar-left">
  <a href="ir/index.jsp"><img  class="centered"  src="ir/Illuminati_png.png" style="height:50px;"></a>
 </div>

			<form action="Servlet" method="get" class="navbar-form  navbar-left"
				role="search">
				<!--<a href="#" ><img  src="illuminati_text.jpg" class="btn" style="width:10%; height:10%"></a>-->
				<div class="form-group">
					<input list="questionType" name="questionType" type="text"
						class="form-control" placeholder="Search" style="width:500px">
					<datalist id="questionType">
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
				<option value="Why was demonetisation implemented">
				<option value="Who is against demonetisation">
					</datalist>
				</div>
				<input id="hideOnReload1" value="Search" class="btn btn-default"
					type="submit" onclick="showDiv()"></input>
			</form>
			<div class="nav navbar-nav navbar-right">
				<!-- <a href="ir/aboutUs.jsp"><img src="ir/IEye.png"
					style="height: 50px;" title="About Us"> </a> -->
					<a class="text-info text-right vertical-align" href="ir/aboutUs.jsp" ><h4 style="margin-right:15px !important">Help</h4></a>

			</div>
		</div>
		<!-- /.navbar-collapse -->
	</div>
	<!-- /.container-fluid --> </nav>
	<div class="container-fluid">
		<br>
		<div class="row" id="hideOnReload2" style="display:block;">	
				<!-- <div class="col-sm-6">
				<div class="well" style="display:block;">One Word Ans</div></div>
                 <div class="loader centered"></div> -->
                 <div class="col-sm-10">
				<div>
					<c:forEach items="${finalAnswers}" var="entry"
						varStatus="loopCount">
						<c:choose>
							<c:when test="${entry.key eq 0}">
								<c:if test="${not empty entry.value}">
									<div>
										<h3>Relevant Answers</h3>
										<div class="well" style="display: block; width: 50%;">
											<c:forEach var="relevantAnswer" items="${entry.value}">
												<p style="font-size:24px">${relevantAnswer}</p>
											</c:forEach>
										</div>
									</div>
								</c:if>
							</c:when>
							<c:otherwise>
								<div>
									<h3>Relevant Tweets</h3>
									<c:forEach var="relevantTweet" items="${entry.value}">
										<div>
											<p style="font-size:16px">${relevantTweet}</p>
											<br/>
										</div>
									</c:forEach>
								</div>
					
					</c:otherwise>
					</c:choose>
					</c:forEach>
				</div>
			</div>	
					</div>
					<div class="col-sm-1"></div>
					<div class="col-sm-1"></div></div>
				    <div id="loaderDiv" style="display:none;" class="loader centered"></div>
<div class="row"></div>
<footer class="footer">
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
	   document.getElementById('hideOnReload1').style.display = "none";
	   document.getElementById('hideOnReload2').style.display = "none";
	}
</script>
</html>