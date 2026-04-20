# 非遗文化交流网站项目

## 项目结构

```
feiyiwenhua/
├── 01-前端/                 # 前端代码 (Vue 3 + Vite)
│   └── feiyiwenhua-frontend/
│       ├── src/            # 源代码
│       ├── public/         # 静态资源
│       ├── package.json    # 依赖配置
│       └── vite.config.js  # Vite配置
│
├── 02-后端/                 # 后端代码 (Spring Boot)
│   ├── src/                # Java源代码
│   │   └── main/
│   │       ├── java/       # 业务代码
│   │       └── resources/  # 配置文件
│   ├── target/             # 编译输出
│   └── pom.xml             # Maven配置
│
├── 03-文档报告/             # 项目文档和报告
│   ├── 项目分析报告.md      # 完整项目分析
│   ├── 功能模块图.html      # 可视化功能模块图
│   ├── 功能模块图-插入版.docx  # Word版功能模块图
│   └── ...                 # 其他文档
│
├── 04-工具脚本/             # 辅助工具和脚本
│   └── create-module-diagram.js  # 生成Word文档脚本
│
└── README.md               # 本文件
```

## 技术栈

### 前端
- **框架**: Vue 3
- **构建工具**: Vite
- **UI组件**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router

### 后端
- **框架**: Spring Boot 2.7.18
- **语言**: Java 11
- **数据库**: MySQL + MyBatis Plus 3.5.9
- **存储**: MinIO 对象存储
- **安全**: JWT + Spring Security

### 数据库表
1. user - 用户表
2. heritage - 非遗项目表
3. category - 分类表
4. master - 传承人表
5. activity - 活动表
6. activity_registration - 活动报名表
7. post - 社区帖子表
8. comment - 评论表
9. favorite - 收藏表
10. notice - 公告表

## 功能模块

### 用户端
- 个人信息管理
- 互动交流（发帖、评论、点赞）
- 了解文化展览
- 线下活动查看
- 活动报名

### 游客端
- 了解文化展览（无需登录）

### 管理员端
- 用户管理
- 文化展览管理（增删改查）
- 互动消息管理（审核、删除）
- 活动管理（发布、编辑、审批）

## 快速启动

### 前端
```bash
cd 01-前端/feiyiwenhua-frontend
npm install
npm run dev
```

### 后端
```bash
cd 02-后端
# 使用IDEA打开，或执行
mvn spring-boot:run
```

## 文档索引

| 文档 | 说明 |
|------|------|
| 项目分析报告.md | 完整项目架构分析 |
| 功能模块图.html | 交互式功能模块图 |
| 开题报告功能模块图.html | 完整可视化版本 |
| 功能模块图-简洁版.html | 简洁表格版本 |
| 功能模块图-文本版.txt | 可复制文本版本 |
| 功能模块图-插入版.docx | 可直接插入开题报告的Word版 |
| 功能模块图.md | Markdown格式模块图 |
| 中期检查报告.docx | 完整中期检查报告Word版 |
| 中期检查报告.md | 中期检查报告Markdown版 |

---
整理时间: 2026-04-15
整理人: 刘府大管家
# IntangibleWenHua..
