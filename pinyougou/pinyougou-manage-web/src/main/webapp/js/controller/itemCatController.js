app.controller("itemCatController", function ($scope, $controller, itemCatService, typeTemplateService) {

    //加载baseController控制器并传入1个作用域，与angularJs运行时作用域相同.
    $controller("baseController",{$scope:$scope});

    //加载列表数据
    $scope.findAll = function(){
        itemCatService.findAll().success(function (response) {
            $scope.list = response;
        });
    };

    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };

    $scope.save = function () {
        var object;

        $scope.entity.typeId = $("#typeTemplateId").val();

        $scope.entity.parentId = $scope.parentId;

        if($scope.entity.id != null){//更新

            object = itemCatService.update($scope.entity);
        } else {//新增
            object = itemCatService.add($scope.entity);
        }
        object.success(function (response) {
            if(response.success){
                $scope.findByParentId($scope.parentId);
            } else {
                alert(response.message);
            }
        });
    };

    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(function (response) {
            $scope.entity = response;

            //设置类型模板
            $("#typeTemplateId").select2("val",$scope.entity.typeId)
        });
    };

    $scope.delete = function () {
        if($scope.selectedIds.length < 1){
            alert("请先选择要删除的记录");
            return;
        }
        if(confirm("确定要删除已选择的记录吗")){
            itemCatService.delete($scope.selectedIds).success(function (response) {
                if(response.success){
                    $scope.findByParentId($scope.parentId);
                    $scope.selectedIds = [];
                } else {
                    alert(response.message);
                }
            });
        }
    };

    $scope.searchEntity = {};//初始为空
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchEntity).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });

    };

    $scope.findByParentId = function (parentId) {
        itemCatService.findByParentId(parentId).success(function (response) {
            $scope.list = response;
        });
    };

    //默认第一级分类
    $scope.grade=1;
    $scope.selectList = function (grade, entity) {//获取当前分类的子分类列表
        $scope.grade = grade;

        $scope.parentId = entity.id;//记录父id
        $scope.parentName = entity.name;

        switch(grade){
            case 1:
                $scope.entity_1 = null;
                $scope.entity_2 = null;
                break;
            case 2:
                $scope.entity_1 = entity;
                $scope.entity_2 = null;
                break;
            default:
                $scope.entity_2 = entity;
        }

        $scope.findByParentId(entity.id);
    };

    $scope.findTypeTemplateList = {data: []};
    $scope.findTypeTemplateList = function () {
        typeTemplateService.selectOptionList().success(function (response) {
            $scope.typeTemplateList = {data:response};
        });
    };

});