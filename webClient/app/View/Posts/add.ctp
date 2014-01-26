<?php echo $this->Form->create('Post', array('type' => 'file')); ?>
<?php echo $this->Form->input('post', array('type' => 'text', 'name' => 'post')); ?>
<?php echo $this->Form->input('username', array('type' => 'hidden', 'value' => 'alabount', 'name' => 'username')); ?>
<?php echo $this->Form->end('finish');