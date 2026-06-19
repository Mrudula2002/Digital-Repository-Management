document
.getElementById("registerForm")
.addEventListener("submit", register);

async function register(e){

e.preventDefault();

const userData = {

username:
document.getElementById("username").value,

email:
document.getElementById("email").value,

password:
document.getElementById("password").value

};

try{

const response = await fetch(
"http://localhost:8080/api/v1/auth/register",
{
method:"POST",
headers:{
"Content-Type":"application/json"
},
body:JSON.stringify(userData)
});

const data=await response.json();

if(response.ok){

alert("Registration successful");

window.location.href="login.html";

}else{

alert(data.message);

}

}catch(error){

alert("Registration failed");

}

}