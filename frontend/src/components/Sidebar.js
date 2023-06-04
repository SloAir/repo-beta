import React from 'react'
import { SidebarContainer, Icon, CloseIcon, SidebarLink, SideBtnWrap, SidebarRoute, SidebarMenu, SidebarWrapper } from './SidebarElements'

const Sidebar = ({isOpen, toggle}) => {
    return (
        <SidebarContainer isOpen={isOpen} onClick={toggle}>
            <Icon onClick={toggle}>
                <CloseIcon/>
            </Icon>
            <SidebarWrapper>
                <SidebarMenu>
                    <SidebarLink to='/aircrafts' onClick={toggle}>
                        Aircrafts
                    </SidebarLink>
                    <SidebarLink to='/airlines' onClick={toggle}>
                        Airlines
                    </SidebarLink>
                </SidebarMenu>
                <SideBtnWrap>
                    <SidebarRoute to="/">Sign In</SidebarRoute>
                </SideBtnWrap>
            </SidebarWrapper>
        </SidebarContainer>
    );
};

export default Sidebar;