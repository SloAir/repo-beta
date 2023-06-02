import axios from 'axios';
import { React, useState, useEffect} from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { UserContext } from "./userContext";
import Header from "./routes/Header";
import Logout from "./routes/Logout";
import Login from "./routes/Login";
import Aircrafts from "./routes/Aircrafts";
import Airlines from './routes/Airlines';
import Airports from './routes/Airports';
import MapContainer from './routes/Map';
import Register from './routes/Register';
import './App.css';
import BarChart from './components/BarChart';


function App() {
  const [user, setUser] = useState(localStorage.user ? JSON.parse(localStorage.user) : null);

  const updateUserData = (userInfo) => {
    localStorage.setItem("user", JSON.stringify(userInfo));
    setUser(userInfo);
  }

  const resetUserData = () => {
    localStorage.removeItem("user");
    setUser(null);
  }

  return (
    <Router>
      <UserContext.Provider value={{
        user: user,
        setUserContext: updateUserData
      }}>
      <Header/>
      <Routes>
        <Route path="/" exact element={<BarChart/>}/>
        <Route path="/aircrafts" exact element={<Aircrafts />}/>
        <Route path="/airlines" exact element={<Airlines />}/>
        <Route path="/airports" exact element={<Airports />}/>
        <Route path="/map" exact element={<MapContainer />}/>
        <Route path="/login" exact element={<Login />}/>
        <Route path="/logout" exact element={<Logout />}/>
        <Route path="/register" exact element={<Register />}/>
      </Routes>
      </UserContext.Provider>
    </Router>
  );
}

export default App;