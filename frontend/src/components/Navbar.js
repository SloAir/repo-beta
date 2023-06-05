import React, { useContext, useState } from 'react';
import { BiPaperPlane } from 'react-icons/bi';
import { FaTimes, FaBars } from 'react-icons/fa';
import { UserContext } from "../userContext";
import { Nav, NavbarContainer, NavLogo, Bars, NavMenu, NavBtn, NavBtnLink, MobileIcon, NavItem, NavLinks, NavBtnAlt } from './NavbarElements'


const Navbar = ({ toggle }) => {

    const { resetUserContext } = useContext(UserContext);

    const handleLogout = () => {
        resetUserContext();
    }

    return (
        <>
        <Nav>
            <NavbarContainer>
                <NavLogo to="/">SloAir</NavLogo>
                <MobileIcon onClick={toggle}>
                    <FaBars />
                </MobileIcon>
                    <NavMenu>
                        <NavItem>
                            <NavLinks to='/aircrafts'>Aircrafts</NavLinks>
                        </NavItem>
                        <NavItem>
                            <NavLinks to='/airlines'>Airlines</NavLinks>
                        </NavItem>
                        <NavItem>
                            <NavLinks to='/airports'>Airports</NavLinks>
                        </NavItem>
                        <NavItem>
                            <NavLinks to='/map'>Map</NavLinks>
                        </NavItem>
                    </NavMenu>
                    <UserContext.Consumer>
                    {context => (
                        Boolean(!context.user)
                        ?
                        <>
                            <NavBtn>
                                <NavBtnLink to="/login">Log In</NavBtnLink>
                            </NavBtn>
                            <NavBtn>
                                <NavBtnLink to="/register">Register</NavBtnLink>
                            </NavBtn>
                        </>
                        :
                        <>
                            <NavBtn>
                                <NavBtnLink to="/logout" onClick={handleLogout} >Log out</NavBtnLink>
                            </NavBtn>
                        </>
                    )}
                </UserContext.Consumer>
            </NavbarContainer>
        </Nav>
        </>
    );
};

export default Navbar;
