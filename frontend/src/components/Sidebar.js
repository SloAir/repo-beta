import React from 'react'
import { SidebarContainer, Icon, CloseIcon, SidebarLink, SideBtnWrap, SidebarRoute, SidebarMenu, SidebarWrapper } from './SidebarElements'
import { UserContext } from '../userContext';

const Sidebar = ({isOpen, toggle}) => {
    return (
        <SidebarContainer isOpen={isOpen} onClick={toggle}>
            <Icon onClick={toggle}>
                <CloseIcon/>
            </Icon>
            <SidebarWrapper>
                <SidebarMenu>
                    <SidebarLink to='/' onClick={toggle}>
                        Home
                    </SidebarLink>
                    <SidebarLink to='/aircrafts' onClick={toggle}>
                        Aircrafts
                    </SidebarLink>
                    <SidebarLink to='/airlines' onClick={toggle}>
                        Airlines
                    </SidebarLink>
                    <SidebarLink to='/airports' onClick={toggle}>
                        Airports
                    </SidebarLink>
                    <SidebarLink to='/map' onClick={toggle}>
                        Map
                    </SidebarLink>
                </SidebarMenu>
                <UserContext.Consumer>
                {context =>(
                    Boolean(context.user === null)
                    ?
                    <>
                        <SideBtnWrap>
                            <SidebarRoute to="/login">Log In</SidebarRoute>
                        </SideBtnWrap>
                        <div>

                        </div>
                        <SideBtnWrap>
                            <SidebarRoute to="/register">Register</SidebarRoute>
                        </SideBtnWrap>
                    </>
                    :
                    <>
                        <SideBtnWrap>
                            <SidebarRoute to="/logout">Log out</SidebarRoute>
                        </SideBtnWrap>
                    </>
                )}
                </UserContext.Consumer>
            </SidebarWrapper>
        </SidebarContainer>
    );
};

export default Sidebar;