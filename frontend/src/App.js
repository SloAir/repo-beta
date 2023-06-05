import axios from 'axios';
import { React, useState, useEffect} from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { UserContext } from "./userContext";
import './App.css';

import Header from "./routes/Header";

import Aircrafts from "./routes/data_display/Aircrafts";
import Airlines from './routes/data_display/Airlines';
import Airports from './routes/data_display/Airports';
import MapContainer from './routes/data_display/Map';

import Logout from "./routes/user/Logout";
import Login from "./routes/user/Login";
import Register from './routes/user/Register';

import BarChart from './components/charts/BarChart';

function App() {
  const [user, setUser] = useState(null);

  const updateUserData = (userInfo) => {
      setUser(userInfo)
  }

  return (
    <BrowserRouter>
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
    </BrowserRouter>
  );
}

export default App;