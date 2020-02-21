<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	String ctx = request.getContextPath();
	request.setAttribute("path", ctx);
%>
<!DOCTYPE html>
<html lang="en">
 <head></head>
 <body>
  <!-- META --> 
  <meta charset="utf-8" /> 
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" /> 
  <!-- PAGE TITLE --> 
  <title>Ethan - Identity</title> 
  <!-- FAVICON --> 
  <link rel="shortcut icon" href="assets/img/favicon.png" /> 
  <!-- FONTS --> 
  <link href="https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700&amp;subset=latin-ext" rel="stylesheet" /> 
  <!-- STYLESHEETS --> 
  <link rel="stylesheet" type="text/css" href="assets/css/plugins.css" /> 
  <link rel="stylesheet" type="text/css" href="assets/css/main.css" />   
  <link rel="stylesheet" href="assets/css/image.css">
  <!-- PRELOADER --> 
  <div class="preloader"> 
   <div class="spinner"></div> 
  </div> 
  <!-- /PRELOADER --> 
  <!-- IMAGE CONTAINER --> 
  <div class="image-container"> 
   <div class="background-img"></div> 
  </div> 
  <!-- /IMAGE CONTAINER --> 
  <!-- CONTENT AREA --> 
  <div class="content-area"> 
   <!-- CONTENT AREA INNER --> 
   <div class="content-area-inner"> 
    <!-- INTRO --> 
    <section id="intro"> 
     <!-- CONTAINER MID --> 
     <div class="container-mid"> 
      <!-- ANIMATION CONTAINER --> 
      <div class="animation-container animation-fade-right" data-animation-delay="0"> 
       <h1>I’m Ethan Kruger and I design Stuff.</h1> 
      </div> 
      <!-- /ANIMATION CONTAINER --> 
      <!-- ANIMATION CONTAINER --> 
      <div class="animation-container animation-fade-right" data-animation-delay="200"> 
       <a href="#about" class="smooth-scroll">Learn More 
        <div class="circle"> 
         <i class="fa fa-angle-down" aria-hidden="true"></i>
         <i class="fa fa-angle-down" aria-hidden="true"></i> 
        </div> </a> 
      </div> 
      <!-- /ANIMATION CONTAINER --> 
     </div> 
     <!-- /CONTAINER MID --> 
    </section> 
    <!-- /INTRO --> 
    <!-- ABOUT --> 
    <section id="about"> 
     <h3 class="headline scroll-animated">最新图片</h3> 
     <div class="grid" id="grid">
		<c:forEach items="${list}" var="image"
			varStatus="ids">
			  <photo-card img="${image.uploadpath }/${image.imageid}_${image.name}" link="${image.uploadpath }/${image.imageid}_${image.name}"></photo-card>
		</c:forEach>
<!--   <photo-card img="assets/img/jq22-1.png" link="#"></photo-card> -->
<!--   <photo-card img="assets/img/jq22-2.png" link="#"></photo-card> -->
<!--   <photo-card img="assets/img/jq22-3.png" link="#"></photo-card> -->
<!--   <photo-card img="assets/img/jq22-4.png" link="#"></photo-card> -->
<!--   <photo-card img="assets/img/jq22-4.png" link="#"></photo-card> -->
<!--   <photo-card img="assets/img/jq22-3.png" link="#"></photo-card> -->
<!--   <photo-card img="assets/img/jq22-2.png" link="#"></photo-card> -->
<!--   <photo-card img="assets/img/jq22-1.png" link="#"></photo-card> -->
</div>
     <!-- /CLIENTS --> 
    </section> 
    <!-- /ABOUT --> 
    <!-- FOOTER --> 
    <section id="footer"> 
     <!-- SOCIAL ICONS --> 
     <ul class="social-icons scroll-animated"> 
      <li><a href="#"><i class="fa fa-facebook" aria-hidden="true"></i><i class="fa fa-facebook" aria-hidden="true"></i></a></li> 
      <li><a href="#"><i class="fa fa-twitter" aria-hidden="true"></i><i class="fa fa-twitter" aria-hidden="true"></i></a></li> 
      <li><a href="#"><i class="fa fa-google-plus" aria-hidden="true"></i><i class="fa fa-google-plus" aria-hidden="true"></i></a></li> 
      <li><a href="#"><i class="fa fa-linkedin" aria-hidden="true"></i><i class="fa fa-linkedin" aria-hidden="true"></i></a></li> 
     </ul> 
     <!-- /SOCIAL ICONS --> 
     <p class="scroll-animated"> &copy; yangtongyue </p> 
    </section> 
    <!-- /FOOTER --> 
   </div> 
   <!-- /CONTENT AREA INNER --> 
  </div> 
  <!-- /CONTENT AREA --> 
  <!-- JAVASCRIPTS --> 
  <script type="text/javascript" src="assets/js/plugins.js"></script> 
  <script type="text/javascript" src="assets/js/main.js"></script>  
  <script src="https://www.jq22.com/jquery/vue.min.js"></script>
 <script type="text/javascript" src="assets/js/image.js"></script>	
 </body>
</html>