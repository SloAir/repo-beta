import React, { useState } from 'react'
import Sidebar from '../components/Sidebar'
import { UserContext } from "../userContext";
import Navbar from '../components/Navbar'

const Header = () => {
  const [isOpen, setIsOpen] = useState(false)

  const toggle = () => {
    setIsOpen(!isOpen)
  }

  return(
    <>
      <Sidebar isOpen={isOpen} toggle={toggle}/>
      <Navbar toggle={toggle}/>
    </>
  );
};

export default Header