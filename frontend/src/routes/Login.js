import { useContext, useState } from 'react';
import { UserContext } from '../userContext';
import { Navigate } from 'react-router-dom';
import axios from 'axios'

function Login(){
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const userContext = useContext(UserContext); 

    async function handleLogin(e) {
        e.preventDefault();
    
        try {
          const response = await axios.post("http://localhost:8000/login/", {
            username: username,
            password: password
          }, {
            //withCredentials: true,
            headers: { 'Content-Type': 'application/json' }
          });
    
          const data = await response.data;
          //const user = JSON.parse(data.user);
          console.log(data);
          const user = "halo"

          if (user._id !== undefined) {
            userContext.setUserContext(data.user);
            console.log(userContext);
          } 
          else {
            setUsername("");
            setPassword("");
            setError(data.message);
          }
        } catch (error) {
          console.error(error);
        }
      }

    return (
        <form onSubmit={handleLogin}>
            {userContext ? <Navigate replace to="/" /> : ""}
            <input type="text" name="username" placeholder="Username"
             value={username} onChange={(e)=>(setUsername(e.target.value))}/>
             <input type="password" name="password" placeholder="Password"
             value={password} onChange={(e)=>(setPassword(e.target.value))}/>
             <input type="submit" name="submit" value="Log in"/>
             <label>{error}</label>
        </form>
    );
}

export default Login;