import { useContext, useState } from 'react';
import { UserContext } from '../../userContext';
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
        <div className={'container my-2 h-100'}>
          <div className={'row d-flex justify-content-center align-items-center h-100'}>
            <div className={'col-lg-12 col-xl-11'}>
              <div className={'div text-black'}>
                <div className={'card-body p-md-5'}>
                  <div className={'row justify-content-center'}>
                    <div className={'col-md-10 col-lg-6 col-xl-5 order-2 order-lg-1'}>
  
                      <form className={'mx-1 mx-md-4" action="/users/validate_login" method="post" id="registrationForm'} onSubmit={handleLogin}>
  
                        {userContext.user ? <Navigate replace to="/" /> : ""}
  
                        <div className={'form-outline mb-1'}>
  
                          <div className={'col input-group align-items-center'}>
  
                            <input className={'form-control border border-dark-subtle rounded-2'} type="text" name="username" placeholder="Username" value={username} onChange={(e)=>(setUsername(e.target.value))}/>
  
                          </div>
  
                        </div>
  
                        <div className={'form-outline mb-1'}>
  
                          <div className={'col input-group align-items-center'}>
  
                            <input className={'form-control border border-dark-subtle rounded-2'} type="password" name="password" placeholder="Password" value={password} onChange={(e)=>(setPassword(e.target.value))}/>
  
                          </div>
  
                        </div>
  
                        <div className={'form-outline mb-1'}>
  
                          <div className={'col input-group align-items-center'}>
  
                            <input className={'form-control border border-dark-subtle rounded-2'} type="submit" name="submit" value="Log in"/>
  
                          </div>
  
                        </div>
  
                        <div className={'form-outline mb-1'}>
  
                          <div className={'col input-group align-items-center'}>
  
                            <label>{error}</label>
  
                          </div>
  
                        </div>
                      </form>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      );
}

export default Login;