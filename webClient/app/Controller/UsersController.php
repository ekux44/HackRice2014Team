<?php
class UsersController extends AppController{
	
	var $uses = array('Post', 'User');

	function show_feed($id = null){
		if($id == null)
			$user = $this->User->findByUsername('alabount');
		else
			$user = $this->User->findByUsername($id);
		$posts = $this->Post->findAllByUsername($user['User']['username']);
		$this->set(compact('posts', 'user'));
	}
	function explore($id = null){
		$user = $this->User->findByUsername('ekuxhausen');
		$posts = $this->Post->findAllByUsername($user['User']['username']);
		$this->set(compact('posts', 'user'));
		$this->render('show_feed');
	}
}

?>