import React, { Component } from 'react';
import { Row, Col, Input, Select, Button, Form } from 'antd';
import { Table,Popconfirm } from 'antd';


class Module extends Component {

    constructor(props) {
        super(props);

        this.columns = [
            { title: 'name', dataIndex: 'name', width: '30%'},
            { title: 'age', dataIndex: 'age' },
            { title: 'address', dataIndex: 'address', },
            { title: 'operation', dataIndex: 'operation', render: this.table_column_opeartion_render }
        ];


        this.state = {
            dataSource: [{
                key: '0',
                name: 'Edward King 0',
                age: '32',
                address: 'London, Park Lane no. 0',
            }, {
                key: '1',
                name: 'Edward King 1',
                age: '32',
                address: 'London, Park Lane no. 1',
            }],
            count: 2,
        };
    }

    table_column_name_render = (text, record, index) => (
        <EditableCell
            value={text}
            onChange={this.onCellChange(index, 'name')}
        />
    )

    table_column_opeartion_render = (text, record, index) => (
        this.state.dataSource.length > 1 ?
            (
                <Popconfirm title="Sure to delete?" onConfirm={() => this.onDelete(index)}>
                    <a href="#">删除</a>
                </Popconfirm>
            ) : null
    )

    table_on_delete=(index)=>{}

    render() {

        const dataSource = this.state.dataSource
        const columns = this.columns
        return (
            <div>
                <Form>
                    <Row>
                        <Col span={5}>
                            <Form.Item labelCol={{ span: 3 }} wrapperCol={{ span: 21 }} label="名称">
                                <Input placeholder="模块名称" />
                            </Form.Item>
                        </Col>
                        <Col span={4}>
                            <Form.Item labelCol={{ span: 5 }} wrapperCol={{ span: 19 }} label="类型">
                                <Select defaultValue="all" style={{ width: 170 }}>
                                    <Select.Option value="all">全部</Select.Option>
                                    <Select.Option value="jar">.JAR</Select.Option>
                                    <Select.Option value="govvy">.GOVVY</Select.Option>
                                    <Select.Option value="js">.JS</Select.Option>
                                    <Select.Option value="py" disabled>.PY</Select.Option>
                                </Select>
                            </Form.Item>
                        </Col>
                        <Col span={2}>
                            <Form.Item>
                                <Button type="primary" htmlType="submit">查询</Button>
                            </Form.Item>
                        </Col>
                    </Row>
                </Form>

            

                <Table bordered dataSource={dataSource} columns={columns} />
            </div>
        )
    }
}


export default Module