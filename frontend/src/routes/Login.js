import { useContext, useState } from 'react';
import { UserContext } from '../userContext';
import { Navigate } from 'react-router-dom';
import axios from 'axios'

function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const userContext = useContext(UserContext);

    async function handleLogin(e) {
        e.preventDefault();

        try {
          const response = await axios.post(process.env.REACT_APP_SERVER_URL + '/login/', {
            username: username,
            password: password
          }, {
            // withCredentials: true,
            headers: { 'Content-Type': 'application/json' }
          });

          const data = await response.data;
          // const user = JSON.parse(data.user);
          console.log(data);

          if (data.user) {
            const { _id, username, password, authenticationKey } = data.user
            userContext.setUserContext({ _id, username, password, authenticationKey });
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
            {userContext.user ? <Navigate replace to="/" /> : ""}
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