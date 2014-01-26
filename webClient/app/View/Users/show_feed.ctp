<style type="text/css">
	.picture{
		border-style: solid;
		border-width: 2px;
		border-radius: 5px;
		border-color: black;
		margin: 20px 20px 5px 3%;
		width: 700px;
		height: auto;
		text-align: center;
	}
	.feed{
		padding: 55px 0px 0px 0px;
		border-style: solid;
		border-color: black;
		border-width: 1px;
	}
	.img-div{
		width: 95%;
		height: auto;
	}
</style>
<div class="col-md-12 col-lg-8 col-lg-offset-2 feed" style="background-color:lightgrey">
<?php foreach($posts as $post): ?>
	<div class="img-div">
		<img class="picture img-responsive" id="<?= $post['Post']['id']?>" src="<?=$post['Post']['path']?>"/>
		<small class="pull-right" style="margin-right:10px"><?= date('M d, Y h:i a', strtotime($post['Post']['created'])); ?></small>
	</div>
	</br>
<?php endforeach; ?>
</div>
<script type="text/javascript">
var latestStep = "<?=$posts[0]['Post']['id']?>";
console.log(latestStep);
setInterval(function(){
    $.ajax({
        url: "/posts/getnew/"+latestStep,
        type: "GET",
        success: function(results) {
            posts = JSON.parse(results);
           	for(var i = 0; i < posts.length; i++){
           		var path = posts[i]['Post']['path'];
           		var created = posts[i]['Post']['created'];
           		$('.feed').prepend('<div class="img-div new_img" style="display:none"><img class="picture" src="'+path+'"/><small class="pull-right" style="margin-right:10px">'+created+'</small></div>');
           		$('.new_img').show(400);
           		$('.new_img').removeClass('new_img');
           	}
           	if(posts.length != 0)
           		latestStep = posts[0]['Post']['id'];
        }
    });
}, 5000);
<?php foreach($posts as $post): ?>
$('#<?=$post['Post']['id']?>').popover(content: "<?=$post['Post']['comment']?>", trigger : 'hover');
<?php endforeach; ?>
</script>