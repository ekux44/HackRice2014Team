<!DOCTYPE html>
<html lang="en">
<head>
	<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap.min.css">
	<script src="/js/jquery-2.1.0.min.js"></script>
	<script src="//netdna.bootstrapcdn.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>
	<style type="text/css">
		.navbar{
			background-color: darkturquoise;
			color: lightgrey;
			padding: 0px;
			margin: 0px;
			border-radius: 0px;
			border-width: 0px;
		}
		.navbar-link{
			color: white;
		}
		body
		{
			background-image:url('http://www.unsigneddesign.com/Seamless_background_textures/1200px/seamlesstexture22_1200.jpg');
			background-repeat:repeat;
		}
	</style>
</head>
<body>
	<nav class="navbar navbar-fixed-top" role="navigation">
		<div class="navbar-header">
			<a class="navbar-brand navbar-link" href="/users/show_feed">Glimpse</a>
		</div>
		<div class="collapse navbar-collapse" id="default_nav">
			<ul class="nav navbar-nav">
				<li><a href="/users/show_feed" class="navbar-link">My Profile</a></li>
				<li><a href="/users/explore" class="navbar-link">Explore</a></li>
			</ul>
			<form class="navbar-form navbar-right" style="padding-right: 20px"role="search">
      			<div class="form-group">
        			<input type="text" class="form-control" placeholder="Search">
      			</div>
      			<button type="submit" class="btn btn-default">Submit</button>
   			 </form>
		</div>
	</nav>
	<div class="container">
		<?php echo $content_for_layout; ?>
	</div>
</body>
</html>