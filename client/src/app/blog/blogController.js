angular.module('blog').controller('BlogCtrl', BlogCtrl);

BlogCtrl.$inject = ['$scope','blogPostService'];

function BlogCtrl(sc, blogPostService) {
	  sc.blog = {
			  posts : []
	  };
	  
	  blogPostService.query({}).$promise.then(
			  function(success) {
				 sc.blog.posts = success;
			  }, 
			  function(error) {
				  console.log("error");
			  });
}
