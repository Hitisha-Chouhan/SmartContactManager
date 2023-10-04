console.log("JS Activated")

const toggleSidebar=()=>{
	if($(".sidebar").is(":visible"))
		{
		//to hide the sidebar
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
		
		}else{
			//to show the sidebar
			$(".sidebar").css("display","block");
			$(".content").css("margin-left","20%");
			
			
		}
	
	
};

const search=()=>{
	//console.log("searching");

	let query=$("#search-input").val();
	console.log(query);

	if(query==""){
		$(".search-result").hide();
	}else{

		console.log(query);
		//sending request to server

		let url=`https://ec2-63-33-239-176.eu-west-1.compute.amazonaws.com:5432/search/${query}`;
		fetch(url).then((response)=>{
			return  response.json();

		}).then((data)=>{
			//data came here
			console.log(data);

			let text=`<div class='list-group'>`
				data.forEach((contact)=> {
					text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'>${contact.name} </a>`
				});


			text+=`</div>`;

			$(".search-result").html(text);
			
			$(".search-result").show();
		});
		
	}
};
