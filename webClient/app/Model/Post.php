<?php
class Post extends AppModel{
	var $belongsTo = array('User' => array('foreignKey' => 'username'));
	var $order = "Post.created DESC";
}
?>