import React, { Component } from 'react';
import { Layout, Menu, Icon } from 'antd';
import {Link} from "react-router-dom"
import "./Index.css"
const { Header, Sider, Content } = Layout;


class Index extends Component {
  state = {
    collapsed: false,
  }
  toggle = () => {
    this.setState({
      collapsed: !this.state.collapsed,
    });
  }

  // toggle(){
  //   this.setState({
  //     collapsed: !this.state.collapsed,
  //   });
  // }

  on_menu_select(item, key, selectedKeys) {
    console.log(item)

  }

  render() {
    return (
      <Layout className="root-main-layout">
        <Sider
          trigger={null}
          collapsible
          collapsed={this.state.collapsed}
        >
          <div className="logo" >这是一个logo</div>
          <Menu theme="dark" mode="inline" defaultSelectedKeys={['1']} onSelect={this.on_menu_select} >
            <Menu.Item key="1">
              <Icon type="area-chart" />
              <Link className="nav-text" to="/dashboard">状态监控</Link>
            </Menu.Item>
            <Menu.Item key="2">
              <Icon type="appstore" />
              <Link className="nav-text" to="/module">模块管理</Link>
            </Menu.Item>
            <Menu.Item key="3">
              <Icon type="file-text" />
              <span className="nav-text">规则管理</span>
            </Menu.Item>
          </Menu>
        </Sider>
        <Layout>
          <Header style={{ background: '#fff', padding: 0 }}>
            <Icon
              className="trigger"
              type={this.state.collapsed ? 'menu-unfold' : 'menu-fold'}
              onClick={this.toggle}
            />

            <h1>
              {/*<Icon type={this.state.menu.icon}/>*/}
              {/*<span>{this.state.menu.text}</span>*/}
            </h1>
          </Header>

          <Content style={{ margin: '24px 16px', padding: 24, background: '#fff', minHeight: 280 }}>
            {this.props.children}
          </Content>
        </Layout>
      </Layout>
    );
  }
}

export default Index
