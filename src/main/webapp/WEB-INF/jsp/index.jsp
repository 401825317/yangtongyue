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
       <h1>Yang Tong Yue.</h1> 
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
     <h3 class="headline scroll-animated">最新集锦</h3> 
     <div class="grid" id="grid">
		<c:forEach items="${list}" var="image"
			varStatus="ids">
			<c:choose>
			<c:when test="${image.type==0}">
				 <photo-card img="${image.uploadpath }" link="${image.uploadpath }"></photo-card>
			</c:when>
			<c:when test="${image.type==1}">
					<video src="${image.uploadpath }" width="150" height="150" preload="preload" controls="controls">
				您的浏览器不支持 video 标签。
				</video>
			</c:when>
			<c:when test="${image.type==2}">
			<audio src="${image.uploadpath }" width="150" height="150" preload="preload" controls="controls">
				您的浏览器不支持 video 标签。
				</audio>
			</c:when>
			<c:otherwise>
			</c:otherwise>
		</c:choose>
		</c:forEach>
</div>
     <!-- /CLIENTS --> 
    </section> 
      <section id="mama">
           <h3 class="headline scroll-animated">妈妈想对你说的</h3> 
      </section>
      <section id="baba">
         <h3 class="headline scroll-animated">爸爸想对你说的</h3> 
         </section>
    <!-- /ABOUT --> 
    <!-- FOOTER --> 
    <section id="footer"> 
     <!-- SOCIAL ICONS --> 
     <ul class="social-icons scroll-animated"> 
<!--       <li><a href="#"><i class="fa fa-facebook" aria-hidden="true"></i><i class="fa fa-facebook" aria-hidden="true"></i></a></li>  -->
<!--       <li><a href="#"><i class="fa fa-twitter" aria-hidden="true"></i><i class="fa fa-twitter" aria-hidden="true"></i></a></li>  -->
<!--       <li><a href="#"><i class="fa fa-google-plus" aria-hidden="true"></i><i class="fa fa-google-plus" aria-hidden="true"></i></a></li>  -->
<!--       <li><a href="#"><i class="fa fa-linkedin" aria-hidden="true"></i><i class="fa fa-linkedin" aria-hidden="true"></i></a></li>  -->
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