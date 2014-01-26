<?php
class PostsController extends AppController{
	var $uses = array('Friend', 'Post');

	function add(){
		if(!empty($this->request->data)){
			$data = $this->request->data;
			foreach($data as $key => $datum){
				CakeLog::write('debug', $key.': '.$datum);
			}
			CakeLog::write('debug', $data['username']);
			CakeLog::write('debug', $data['post']);
			$base = $data['post'];
			$binary=base64_decode($base);
			header('Content-Type: bitmap; charset=utf-8');
			$time = microtime();
			$file = fopen(WWW_ROOT.'user_imgs'.DS.$data['username'].DS.$time.'.jpg', 'w');
			fwrite($file, $binary);
			fclose($file);
			$post = array('Post' => array());
			$post['Post']['username'] = $data['username'];
			$post['Post']['path'] = '/user_imgs'.DS.$data['username'].DS.$time.'.jpg';
			$this->Post->save($post);
		}
	}
	function getnew($latestStep){
		$posts = $this->Post->find('all', array('conditions' => array(
			'Post.id >' => $latestStep)));
		array_walk($posts, function(&$var){
			$var['Post']['created'] = date('M d, Y h:i a', strtotime($var['Post']['created']));
		});
		echo json_encode($posts);
		die;
	}
	function getRandom($dateTime = null, $friend = null){
		$this->layout = false;
		$conditions = array();
		if($dateTime != null){
			$date = date('Y-m-d', strtotime($dateTime));
			$firstTime = date('Y-m-d H:i:s', strtotime($date.' 00:00:00'));
			$lastTime = date('Y-m-d H:i:s', strtotime($date.' 23:59:59'));
			$conditions['Post.created >='] = $firstTime;
			$conditions['Post.created <='] = $lastTime;
		}
		if($friend != null){
			$friends = $this->Friend->find('list', array(
				'conditions' => array('Friend.username1' => $friend),
				'fields' => array('Friend.username1', 'Friend.username1')));
			$conditions['Post.username'] = $friends;	
		}
		$posts = $this->Post->find('all', array('conditions' => $conditions));
		$index = rand(0, count($posts) - 1);
		$this->response->file(WWW_ROOT.$posts[$index]['Post']['path']);
		return $this->response;
	}
}
?>